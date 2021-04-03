package com.github.nsilbernagel.discordbot.task.validation;

import com.github.nsilbernagel.discordbot.message.validation.MessageValidationException;
import com.github.nsilbernagel.discordbot.task.TaskRequest;

public interface Validator<r extends TaskRequest> {
  /**
   * validate a request
   * @return the error message
   */
  boolean validate(r request) throws MessageValidationException;
}
