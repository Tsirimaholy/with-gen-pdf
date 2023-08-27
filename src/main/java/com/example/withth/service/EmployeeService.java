package com.example.withth.service;

import com.example.withth.controller.request.EmployeeFilter;
import com.example.withth.models.employeeManagement.entity.Employee;
import com.example.withth.models.employeeManagement.entity.Phone;
import com.example.withth.models.employeeManagement.entity.Sex;
import com.example.withth.repository.employeeManagement.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;
    private final CompanyService service;
    @Getter
    @Setter
    private EmployeeFilter lastFilterUsed = new EmployeeFilter();

    public EmployeeService(EmployeeRepository repository, CompanyService service) {
        this.repository = repository;
        this.service = service;
    }

    public List<Employee> getEmployees() {
        return repository.findAll();
    }

    public void save(Employee employee) {
        List<Phone> phoneLinkedToEmployee = employee.getPhones();
        for (int i = 0; i < phoneLinkedToEmployee.size(); i++) {
            Phone phone = phoneLinkedToEmployee.get(i);
            phone.setEmployee(employee);
        }
        employee.setPhones(phoneLinkedToEmployee);
        repository.save(employee);
    }

    public Employee findById(Long id) {
        Optional<Employee> byId = repository.findById(id);
        return byId.orElseGet(Employee::new);
    }

    public List<Employee> filter(String name, String function, Sex sex, String orderBy, Date entryDateStart, String direction, Date entryDateEnd, Date departureDateStart, Date departureDateEnd) {
        String sexQuery = (sex != null) ? sex.toString() : null;
        lastFilterUsed.setFirstName(name);
        lastFilterUsed.setFunction(function);
        lastFilterUsed.setSex(sexQuery);
        lastFilterUsed.setOrderBy(orderBy);
        lastFilterUsed.setDirection(direction);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);


        return repository.filterByNameOrFunction(name, function, sex, entryDateStart, entryDateEnd, departureDateStart, departureDateEnd, sort);
    }

    public void exportToCSV(HttpServletResponse response, List<Employee> employees) throws IOException {
        String csvFileName = "employees.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + csvFileName + "\"");
        //create a csv writer
        String[] headers = {"Matricule", "Nom", "DateDeNaissance",
                "E-mail", "ChildrenInCharges", "EntryDate",
                "CIN", "Address", "PhoneNumbers",
                "CNAPS", "Position", "ProfessionalCategory",
                "Sex"};
        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader(headers))) {
            //write each employee to the csv file
            for (Employee employee : employees) {
                csvPrinter.printRecord(
                        employee.getMatriculate(), employee.getFirstName(), employee.getBirthDate(),
                        employee.getPrivateMail(), employee.getChildrens(), employee.getEntryDate(),
                        employee.getCin(), employee.getAddress(), employee.getPhones(),
                        employee.getCnaps(), employee.getFunction(), employee.getProfessionalCategory(),
                        employee.getSex());
            }
        }
    }

    public String parseEmployeeInfoTemplate(
            Employee employee,
                                            HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(templateResolver);

        WebContext context = createContext(servletRequest, servletResponse);
        context.setVariable("employee", employee);
        context.setVariable("company", service.getCompanyDetails(1L));
        return templateEngine.process("employee_infos", context);
    }
    public static WebContext createContext(HttpServletRequest req, HttpServletResponse res) {
        var application = JakartaServletWebApplication.buildApplication(req.getServletContext());
        var exchange =application.buildExchange(req, res);
        return new WebContext(exchange);
    }
}
