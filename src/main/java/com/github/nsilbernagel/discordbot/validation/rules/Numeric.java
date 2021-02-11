package com.github.nsilbernagel.discordbot.validation.rules;

import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.MessageValidationException;

import org.springframework.stereotype.Component;

@Component
public class Numeric
    extends AValidationRule<com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric> {

  protected boolean validateParam() {
    if (!this.commandParam.isPresent()) {
      return true;
    } else {
      try {
        Integer.parseInt(this.commandParam.get());
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  public Class<com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric> getCorrespondingAnnotation() {
    return com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric.class;
  }

  protected void handleInvalid() throws MessageValidationException {
    throw new TaskException(this.commandField.getAnnotation(
        com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric.class).value());
  }
}
