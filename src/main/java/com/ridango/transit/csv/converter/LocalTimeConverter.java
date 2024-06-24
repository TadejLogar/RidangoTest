package com.ridango.transit.csv.converter;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeConverter extends AbstractBeanField<LocalTime, String> {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    protected LocalTime convert(String value) {
        return LocalTime.parse(value, formatter);
    }
}