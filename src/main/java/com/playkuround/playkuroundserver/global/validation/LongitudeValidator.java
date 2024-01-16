package com.playkuround.playkuroundserver.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LongitudeValidator implements ConstraintValidator<Longitude, Double> {

    @Override
    public void initialize(Longitude constraintAnnotation) {
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return -180 <= value && value <= 180;
    }

}
