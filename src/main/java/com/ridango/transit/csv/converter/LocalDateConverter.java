package com.ridango.transit.csv.converter;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;

public class LocalDateConverter extends AbstractBeanField<LocalDate, Integer> {

    @Override
    protected LocalDate convert(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        int intValue = Integer.parseInt(value);
        int year = intValue / 10000;
        int month = (intValue % 10000) / 100;
        int day = intValue % 100;

        return LocalDate.of(year, month, day);
    }
}