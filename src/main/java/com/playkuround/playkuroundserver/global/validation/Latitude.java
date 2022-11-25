package com.playkuround.playkuroundserver.global.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LatitudeValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Latitude {

    String message() default "잘못된 위도 값입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
