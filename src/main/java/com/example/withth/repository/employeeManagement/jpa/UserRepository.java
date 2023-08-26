package com.example.withth.repository.employeeManagement.jpa;

import com.example.withth.models.employeeManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}