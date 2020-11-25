package com.github.nsilbernagel.discordbot.message;

/**
 * An Exception to be thrown when a task is missing any data to be executed
 */
public class MissingDataException extends TaskLogicException {
  final static long serialVersionUID = 3L;

  public MissingDataException(String errorMessage) {
    super(errorMessage);
  }
}
