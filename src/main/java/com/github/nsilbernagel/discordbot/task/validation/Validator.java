package com.github.nsilbernagel.discordbot.task.validation;

import com.github.nsilbernagel.discordbot.task.TaskRequest;

public interface Validator<t extends TaskRequest> {
  /**
   * validate a resource
   */
  boolean validate(t resource);
}
