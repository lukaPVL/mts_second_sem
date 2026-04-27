package com.example.validation;

import com.example.entity.Task;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DueDateValidator implements ConstraintValidator<DueDateNotBeforeCreation, Task> {

  @Override
  public boolean isValid(Task task, ConstraintValidatorContext constraintValidatorContext) {
    if (task.getDueDate() == null) {
      return true;
    }

    if (task.getCreatedAt() == null) {
      return true;
    }

    return !task.getDueDate().isBefore(task.getCreatedAt().toLocalDate());
  }
}
