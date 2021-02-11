package com.github.nsilbernagel.discordbot.validation.rules;

import java.lang.annotation.Annotation;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.ParamRule;

public abstract class AbstractValidationRule<A extends Annotation> {
  abstract public boolean validate(Optional<String> command);

  abstract public Class<A> handlesAnnotation();

  public void handleInvalid(A paramRuleAnnotation) throws TaskException {
    throw new TaskException(paramRuleAnnotation.value());
  }

  public void validateAndHandle(Optional<String> command, A paramRuleAnnotation) {
    boolean valid = this.validate(command);
    if (!valid) {
      this.handleInvalid(paramRuleAnnotation);
    }
  }
}
