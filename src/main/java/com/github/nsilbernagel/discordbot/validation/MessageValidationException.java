package com.github.nsilbernagel.discordbot.validation;

public class MessageValidationException extends RuntimeException {
  final static long serialVersionUID = 6L;

  public MessageValidationException(String errorMessage) {
    super(errorMessage);
  }
}
