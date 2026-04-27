package com.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DueDateValidator.class)
public @interface DueDateNotBeforeCreation {

  String message() default "Due date cannot be before creation date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}