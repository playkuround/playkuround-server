package com.playkuround.playkuroundserver.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LatitudeValidator implements ConstraintValidator<Latitude, Double> {

    @Override
    public void initialize(Latitude constraintAnnotation) {
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return -90 <= value && value <= 90;
    }

}
