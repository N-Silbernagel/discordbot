package com.github.nsilbernagel.discordbot.task.validation;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;

public abstract class ValidationRule {
  public abstract boolean validate(CommandParam commandParam);
}
