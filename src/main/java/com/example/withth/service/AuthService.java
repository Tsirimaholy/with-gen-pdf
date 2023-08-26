package com.example.withth.service;

import com.example.withth.models.employeeManagement.entity.User;
import com.example.withth.repository.employeeManagement.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository repository;

    public User login(User user){
        return repository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(()->new RuntimeException("Unauthorized"));
    }
}
