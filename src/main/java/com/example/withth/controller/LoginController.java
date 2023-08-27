package com.example.withth.controller;

import com.example.withth.controller.request.LoginDetails;
import com.example.withth.models.employeeManagement.entity.Employee;
import com.example.withth.models.employeeManagement.entity.User;
import com.example.withth.repository.employeeManagement.EmployeeRepository;
import com.example.withth.repository.employeeManagement.UserRepository;
import com.example.withth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class LoginController {
    private final AuthService service;


    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("AuthUsername");
        session.removeAttribute("AuthPassword");
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDetails loginDetails, HttpSession session) {
        User authenticatedUser = service.login(loginDetails.getUsername(), loginDetails.getPassword());
        if (authenticatedUser==null){
            return "redirect:/login";
        }
        session.setAttribute("AuthUsername", authenticatedUser.getUsername());
        session.setAttribute("AuthPassword", authenticatedUser.getPassword());
        return "redirect:/";
    }
}
