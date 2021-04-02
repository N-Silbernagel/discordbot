package com.github.nsilbernagel.discordbot.message.validation;

import com.github.nsilbernagel.discordbot.message.validation.MessageValidationException;

import com.github.nsilbernagel.discordbot.task.validation.rules.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class Required
    extends ValidationRule<com.github.nsilbernagel.discordbot.message.validation.annotations.Required> {

  protected boolean validateParam() {
    return this.commandParam.isPresent();
  }

  public Class<com.github.nsilbernagel.discordbot.message.validation.annotations.Required> getCorrespondingAnnotation() {
    return com.github.nsilbernagel.discordbot.message.validation.annotations.Required.class;
  }

  protected void handleInvalid() throws MessageValidationException {
    throw new MessageValidationException(this.commandField.getAnnotation(
        com.github.nsilbernagel.discordbot.message.validation.annotations.Required.class).value());
  }
}
