/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.domain;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = URLValidator.URLValidatorImpl.class)
@Documented
@Pattern(regexp = "^(http|https)://[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,})+(?:/[^\\s]*)?$", message = "Invalid URL")
public @interface URLValidator 
{

    String message() default "Invalid URL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class URLValidatorImpl implements ConstraintValidator<URLValidator, String> {
        @Override
        public void initialize(URLValidator constraintAnnotation) {
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value != null && value.matches("^(http|https)://[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,})+(?:/[^\\s]*)?$");
        }
    }
}

