package com.github.nsilbernagel.discordbot.message;

/**
 * An exception to be thrown when some logic inside a task handler fails. These
 * exception are caught in MessageCreateEventListener
 */
public class TaskException extends RuntimeException {
  final static long serialVersionUID = 3L;

  private Throwable actualException;

  public TaskException() {
    super();
  }

  public TaskException(String errorMessage) {
    super(errorMessage);
  }

  public TaskException(Throwable e) {
    super();
    this.actualException = e;
    this.log();
  }

  public TaskException(String errorMessage, Throwable e) {
    super(errorMessage);
    this.actualException = e;
    this.log();
  }

  public boolean hasMessage() {
    return this.getMessage().length() > 0;
  }

  /**
   * Log the actual error. Only to stderr for now. Might want to implement a file
   * error logging or something similar
   */
  private void log() {
    this.actualException.printStackTrace();
  }
}
