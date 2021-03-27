package com.github.nsilbernagel.discordbot.task;

import com.github.nsilbernagel.discordbot.task.TaskException;

public class FalseInputException extends TaskException {
  static final long serialVersionUID = 1L;

  public FalseInputException(String errorMessage) {
    super(errorMessage);
  }
}
