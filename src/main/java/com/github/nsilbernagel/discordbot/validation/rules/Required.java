package com.github.nsilbernagel.discordbot.validation.rules;

import com.github.nsilbernagel.discordbot.message.TaskException;

import org.springframework.stereotype.Component;

@Component
public class Required
    extends AValidationRule<com.github.nsilbernagel.discordbot.validation.rules.annotations.Required> {

  protected boolean validateParam() {
    return this.commandParam.isPresent();
  }

  public Class<com.github.nsilbernagel.discordbot.validation.rules.annotations.Required> getCorrespondingAnnotation() {
    return com.github.nsilbernagel.discordbot.validation.rules.annotations.Required.class;
  }

  protected void handleInvalid() {
    throw new TaskException(this.commandField.getAnnotation(
        com.github.nsilbernagel.discordbot.validation.rules.annotations.Required.class).value());
  }
}
