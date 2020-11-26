package com.github.nsilbernagel.discordbot.message;

public class FalseInputException extends TaskLogicException {
  static final long serialVersionUID = 1L;

  public FalseInputException(String errorMessage) {
    super(errorMessage);
  }
}
