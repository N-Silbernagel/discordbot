package com.github.nsilbernagel.discordbot.validation.rules;

import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.MessageValidationException;

import org.springframework.stereotype.Component;

@Component
public class Numeric
    extends ValidationRule<com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric> {

  protected boolean validateParam() {
    if (this.commandParam.isEmpty()) {
      return true;
    } else {
      try {
        Integer commandParamInteger = Integer.parseInt(this.commandParam.get());
        return validateMinMax(commandParamInteger);
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  private boolean validateMinMax(Integer commandParam) {
    long min = this.commandField.getAnnotation(
        com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric.class).min();
    long max = this.commandField.getAnnotation(
        com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric.class).max();

    return commandParam >= min && commandParam <= max;
  }

  public Class<com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric> getCorrespondingAnnotation() {
    return com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric.class;
  }

  protected void handleInvalid() throws MessageValidationException {
    throw new TaskException(this.commandField.getAnnotation(
        com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric.class).value());
  }
}
