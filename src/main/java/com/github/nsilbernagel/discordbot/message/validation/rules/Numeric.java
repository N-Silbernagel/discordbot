package com.github.nsilbernagel.discordbot.message.validation.rules;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.task.validation.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class Numeric extends ValidationRule {
  @Override
  public boolean validate(CommandParam commandParam) {
    if (commandParam.getRaw() == null){
      return true;
    }

    try {
      Integer.parseInt(commandParam.getRaw());
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
