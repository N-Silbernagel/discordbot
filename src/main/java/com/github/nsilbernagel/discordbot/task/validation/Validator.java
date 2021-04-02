package com.github.nsilbernagel.discordbot.task.validation;

import com.github.nsilbernagel.discordbot.task.TaskRequest;

public interface Validator<r extends TaskRequest> {
  /**
   * validate a request
   * @return the error message
   */
  String validate(r request) throws ValidationException;
}
