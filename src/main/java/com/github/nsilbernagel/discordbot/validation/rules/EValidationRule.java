package com.github.nsilbernagel.discordbot.validation.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Required;

public enum EValidationRule {
  REQUIRED(Required.class) {
    protected boolean validateParam() {
      return this.commandParam.isPresent();
    }

    public void handleInvalid() {
      throw new TaskException(this.commandField.getAnnotation(Required.class).value());
    }
  };

  private final Class<? extends Annotation> validationAnnotation;
  protected Optional<String> commandParam;
  protected Field commandField;

  EValidationRule(Class<? extends Annotation> validationAnnotation) {
    this.validationAnnotation = validationAnnotation;
  };

  public Class<? extends Annotation> getCorrespondingAnnotation() {
    return this.validationAnnotation;
  }

  protected abstract boolean validateParam();

  public boolean validate(Optional<String> commandParam, Field commandField) {
    this.commandParam = commandParam;
    this.commandField = commandField;

    return this.validateParam();
  };

  public abstract void handleInvalid();
}
