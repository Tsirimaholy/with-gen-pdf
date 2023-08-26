package com.example.withth.service;

import com.example.withth.models.employeeManagement.entity.User;
import com.example.withth.repository.employeeManagement.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository repository;

    public User login(String username, String password){
        return repository.findByUsernameAndPassword(username, password)
                .orElseThrow(()->new RuntimeException("Unauthorized"));
    }
}
