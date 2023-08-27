package com.example.withth.service.utils;

import java.time.LocalDate;
import java.time.Period;

public class DateUtils {
    public static int calculateAgeAtExactBirthday(LocalDate birthdate, LocalDate currentDate) {
        if (currentDate == null) {
            currentDate = LocalDate.now();
        }
        if (birthdate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }

        return Period.between(birthdate, currentDate).getYears();
    }
}
