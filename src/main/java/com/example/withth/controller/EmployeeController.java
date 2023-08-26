package com.example.withth.controller;

import com.example.withth.controller.request.EmployeeFilter;
import com.example.withth.models.employeeManagement.entity.CountryCode;
import com.example.withth.models.employeeManagement.entity.Employee;
import com.example.withth.models.employeeManagement.entity.Phone;
import com.example.withth.models.employeeManagement.entity.Sex;
import com.example.withth.service.CountryCodeService;
import com.example.withth.service.EmployeeService;
import com.example.withth.service.PhoneService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
@AllArgsConstructor
public class EmployeeController extends AuthBaseController{
    private final EmployeeService service;
    private final CountryCodeService countryCodeService;
    private final PhoneService phoneService;

    @ModelAttribute("employeeList")
    public List<Employee> getEmployees() {
        return service.getEmployees();
    }
    @ModelAttribute("countryCodes")
    public List<CountryCode> getCountryCodes(){
        return countryCodeService.findAll();
    }

    @ModelAttribute("sexList")
    public List<String> getSex() {
        return Arrays.stream(Sex.values()).map(Sex::toString).toList();
    }


    @RequestMapping("/")
    public ModelAndView getEmployee() {
        ModelAndView modelAndView = new ModelAndView("employee/index");
        modelAndView.addObject("filter", new EmployeeFilter());
        return modelAndView;
    }

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> getPdf() throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(service.parseEmployeeInfoTemplate());
        renderer.layout();
        renderer.createPDF(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=template.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    @GetMapping("/employee-details")
    @Deprecated
    public ModelAndView getEmployeeDetails(@RequestParam(name = "employeeId") Long id) {
        ModelAndView modelAndView = new ModelAndView("employee/employee-details");
        Employee employee = service.findById(id);
        modelAndView.addObject("employee", employee);
        return modelAndView;
    }

    @GetMapping("/employee/{employeeId}")
    public ModelAndView getEmployeeDetails2(@PathVariable(name = "employeeId") Long id) {
        ModelAndView modelAndView = new ModelAndView("employee/employee-details");
        Employee employee = service.findById(id);
        modelAndView.addObject("employee", employee);
        return modelAndView;
    }

    @PostMapping("/filter")
    public String filter(@ModelAttribute EmployeeFilter filter, Model model) {
        // when running a query like filter?name=&function=k
        // the query params of name will be an empty string
        if (Objects.equals(filter.getFirstName(), "")) {
            filter.setFirstName(null);
        }
        if (Objects.equals(filter.getSex(), "")) {
            filter.setSex(null);
        }
        if (filter.getFunction().isEmpty()) {
            filter.setFunction(null);
        }

        Sex sexQuery;
        List<String> sexList = Arrays.stream(Sex.values()).map(Enum::toString).toList();
        if (!sexList.contains(filter.getSex()) || filter.getSex() == null) {
            sexQuery = null;
        } else {
            sexQuery = Sex.valueOf(filter.getSex());
        }

        model.addAttribute("filter", filter);
        List<Employee> listEmployees = service.filter(
                filter.getFirstName(), filter.getFunction(), sexQuery,
                filter.getOrderBy(), filter.getEntryDate(), filter.getDirection(),
                filter.getEntryDateEnd(), filter.getDepartureDateStart(), filter.getDepartureDateEnd());
        model.addAttribute("employeeList", listEmployees);
        return "employee/index";
    }


    @GetMapping("/employee/export")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        EmployeeFilter lastFilterUsed = service.getLastFilterUsed();
        Sex sex;
        if (lastFilterUsed.getSex() == null)
            sex = null;
        else sex = Sex.valueOf(lastFilterUsed.getSex());

        List<Employee> filteredEmployee = service.filter(
                lastFilterUsed.getFirstName(), lastFilterUsed.getFunction(), sex,
                lastFilterUsed.getOrderBy(), lastFilterUsed.getEntryDate(),lastFilterUsed.getDirection(),
                lastFilterUsed.getEntryDateEnd(), lastFilterUsed.getDepartureDateStart(), lastFilterUsed.getDepartureDateEnd());
        service.exportToCSV(response, filteredEmployee);
    }

    @PostMapping(value = "/save")
    public String save(@ModelAttribute Employee employee, @ModelAttribute("file") MultipartFile file, Model model) throws IOException {
        if (!file.isEmpty()) {
            employee.setProfilePicture(file.getOriginalFilename());
            employee.setContent(file.getBytes());
            employee.setSize(file.getSize());
        }
        /* ensure that only one instance of country code with the specified content is stored */
        List<Phone> mappedPhone = employee.getPhones().stream().map(phone -> {
            CountryCode byContent = countryCodeService.findByContent(phone.getCountryCode());
            if (byContent != null) {
                phone.setCountryCode(byContent);
            }
            return phone;
        }).toList();
        employee.setPhones(mappedPhone);
        // phone validation
        for (int i = 0; i < mappedPhone.size(); i++) {
            Phone phone = mappedPhone.get(i);
            String completePhone = phone.getCountryCode().getContent()+phone.getNumber();
            List<Phone> allPhoneNumber = phoneService.findAllPhoneNumber(phone.getCountryCode().getContent(), phone.getNumber());
            if (!allPhoneNumber.isEmpty()){
                model.addAttribute("error", "Phone number already exist");
                model.addAttribute("employee", new Employee());
                return "employee/employee-form";
            }
            if (phone.getNumber().length()!=10){
                model.addAttribute("error", "A phone number must contain 10 characters");
                model.addAttribute("employee", new Employee());
                return "employee/employee-form";
            }
            if (completePhone.matches("\\d+")){
                model.addAttribute("error", "A phone should be only numbers");
                model.addAttribute("employee", new Employee());
                return "employee/employee-form";
            }
        }

        service.save(employee);
        return "redirect:/";
    }

    @PostMapping(value = "/edit")
    public String updateEmployee(@ModelAttribute Employee employee, @ModelAttribute("file") MultipartFile file) throws IOException {
        byte[] content;
        if (file.isEmpty()) {
            Employee oldEmployee = service.findById(employee.getId());
            content = oldEmployee.getContent();
        } else {
            employee.setSize(file.getSize());
            employee.setProfilePicture(file.getOriginalFilename());
            content = file.getBytes();
        }
        employee.setContent(content);
        service.save(employee);
        return "redirect:/";
    }


    @GetMapping("/image")
    public void showImage(@Param("id") Long id, HttpServletResponse response, Employee employee) throws IOException {
        employee = service.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
        byte[] content = employee.getContent();
        if (content != null) {
            response.getOutputStream().write(employee.getContent());
            response.getOutputStream().close();
        }
    }

    @GetMapping("/employee-form")
    public ModelAndView addEmployeeForm() {
        ModelAndView modelAndView = new ModelAndView("employee/employee-form");
        modelAndView.addObject("employee", new Employee());
        return modelAndView;
    }

    @GetMapping("/edit")
    public ModelAndView editEmployeeForm(@Param("id") Long id) {
        Employee employee = service.findById(id);
        ModelAndView modelAndView = new ModelAndView("employee/edit");
        modelAndView.addObject("employee", employee);
        return modelAndView;
    }
}
