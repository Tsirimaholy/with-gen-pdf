package com.example.withth.service.utils;

import java.time.LocalDate;
import java.time.Period;

public class DateUtils {
    public static int calculateAgeAtExactBirthday(LocalDate birthdate, LocalDate currentDate) {
        if (birthdate != null && currentDate != null) {
            if (
                    currentDate.getMonth().getValue() > birthdate.getMonth().getValue() ||
                    (
                            currentDate.getMonth().getValue() == birthdate.getMonth().getValue() &&
                            currentDate.getDayOfMonth() >= birthdate.getDayOfMonth()
                    )
            ) {
                return Period.between(birthdate, currentDate).getYears();
            } else {
                return Period.between(birthdate.minusYears(1), currentDate).getYears();
            }
        }
        return 0;
    }
}
