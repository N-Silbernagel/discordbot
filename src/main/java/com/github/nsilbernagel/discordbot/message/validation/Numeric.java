package com.github.nsilbernagel.discordbot.message.validation;

import com.github.nsilbernagel.discordbot.task.TaskException;
import com.github.nsilbernagel.discordbot.message.validation.MessageValidationException;

import com.github.nsilbernagel.discordbot.task.validation.rules.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class Numeric
    extends ValidationRule<com.github.nsilbernagel.discordbot.message.validation.annotations.Numeric> {

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
        com.github.nsilbernagel.discordbot.message.validation.annotations.Numeric.class).min();
    long max = this.commandField.getAnnotation(
        com.github.nsilbernagel.discordbot.message.validation.annotations.Numeric.class).max();

    return commandParam >= min && commandParam <= max;
  }

  public Class<com.github.nsilbernagel.discordbot.message.validation.annotations.Numeric> getCorrespondingAnnotation() {
    return com.github.nsilbernagel.discordbot.message.validation.annotations.Numeric.class;
  }

  protected void handleInvalid() throws MessageValidationException {
    throw new MessageValidationException(this.commandField.getAnnotation(
        com.github.nsilbernagel.discordbot.message.validation.annotations.Numeric.class).value());
  }
}
