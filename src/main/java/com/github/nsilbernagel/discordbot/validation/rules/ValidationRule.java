package com.github.nsilbernagel.discordbot.validation.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.validation.MessageValidationException;

public abstract class ValidationRule<A extends Annotation> {
  protected Optional<String> commandParam;
  protected Field commandField;

  /**
   * Get the Annotation belonging to the validation Rule
   *
   * @return the annotation
   */
  abstract public Class<A> getCorrespondingAnnotation();

  abstract protected boolean validateParam();

  abstract protected void handleInvalid() throws MessageValidationException;

  public void validate(Optional<String> commandParam, Field commandField) throws MessageValidationException {
    this.commandParam = commandParam;
    this.commandField = commandField;

    boolean paramValid = this.validateParam();

    if (!paramValid) {
      this.handleInvalid();
    }
  }
}
