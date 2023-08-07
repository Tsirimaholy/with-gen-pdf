package com.example.withth.service;

import com.example.withth.models.entity.CountryCode;
import com.example.withth.models.entity.Phone;
import com.example.withth.repository.PhoneRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PhoneService {
    private final PhoneRepository phoneRepository;
    public List<Phone> findAllPhoneNumber(String countryCode, String number){
        return phoneRepository.findByCountryCodeContentAndNumber(countryCode, number);
    }
}