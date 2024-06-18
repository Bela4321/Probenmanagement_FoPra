package de.unimarburg.samplemanagement.utils;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class DoubleToLongConverter implements Converter<Double, Long> {
    @Override
    public Result<Long> convertToModel(Double value, ValueContext context) {
        if (value == null) {
            return Result.ok(null);
        }
        return Result.ok(value.longValue());
    }

    @Override
    public Double convertToPresentation(Long value, ValueContext context) {
        if (value == null) {
            return null;
        }
        return value.doubleValue();
    }
}