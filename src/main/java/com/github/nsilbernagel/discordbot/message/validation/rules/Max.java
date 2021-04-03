package com.github.nsilbernagel.discordbot.message.validation.rules;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.task.validation.ValidationRule;

public class Max extends ValidationRule {
  private final int threshold;

  public Max(int threshold) {
    super();

    this.threshold = threshold;
  }

  @Override
  public boolean validate(CommandParam commandParam) {
    if(commandParam.getRaw() == null) {
      return true;
    }

    try {
      return Integer.parseInt(commandParam.getRaw()) <= threshold;
    } catch (NumberFormatException e) {
      return commandParam.getRaw().length() <= threshold;
    }
  }
}
