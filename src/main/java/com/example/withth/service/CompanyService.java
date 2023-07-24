package com.example.withth.service;

import com.example.withth.models.entity.Company;
import com.example.withth.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public void saveCompanyDetails(Company company) {
        companyRepository.save(company);
    }
    public Company getCompanyDetails(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }
}
