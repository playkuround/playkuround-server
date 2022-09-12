package com.playkuround.playkuroundserver.global.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LongitudeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Longitude {

    String message() default "잘못된 경도 값입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}