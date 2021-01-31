package com.github.nsilbernagel.discordbot.message;

/**
 * An exception to be thrown when some logic inside a task handler fails. These
 * exception are caught in MessageCreateEventListener
 */
public class TaskException extends RuntimeException {
  final static long serialVersionUID = 3L;

  public TaskException() {
    super();
  }

  public TaskException(String errorMessage) {
    super(errorMessage);
  }

  public boolean hasMessage() {
    return this.getMessage().length() > 0;
  }
}
