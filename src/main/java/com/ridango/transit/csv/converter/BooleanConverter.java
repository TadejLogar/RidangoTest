package com.ridango.transit.csv.converter;

import com.opencsv.bean.AbstractBeanField;

public class BooleanConverter extends AbstractBeanField<Boolean, Integer> {

    @Override
    protected Boolean convert(String value) {
        return "1".equals(value);
    }
}