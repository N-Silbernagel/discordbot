package com.github.nsilbernagel.discordbot.validation.rules;

import com.github.nsilbernagel.discordbot.validation.MessageValidationException;

import org.springframework.stereotype.Component;

@Component
public class Required
    extends ValidationRule<com.github.nsilbernagel.discordbot.validation.rules.annotations.Required> {

  protected boolean validateParam() {
    return this.commandParam.isPresent();
  }

  public Class<com.github.nsilbernagel.discordbot.validation.rules.annotations.Required> getCorrespondingAnnotation() {
    return com.github.nsilbernagel.discordbot.validation.rules.annotations.Required.class;
  }

  protected void handleInvalid() throws MessageValidationException {
    throw new MessageValidationException(this.commandField.getAnnotation(
        com.github.nsilbernagel.discordbot.validation.rules.annotations.Required.class).value());
  }
}
