package com.github.nsilbernagel.discordbot.message.validation;

import com.github.nsilbernagel.discordbot.task.TaskException;

public class MessageValidationException extends TaskException {
  final static long serialVersionUID = 6L;

  public MessageValidationException(String errorMessage) {
    super(errorMessage);
  }
}
