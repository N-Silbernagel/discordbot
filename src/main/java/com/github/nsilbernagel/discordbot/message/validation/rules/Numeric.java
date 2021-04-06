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
    } catch (NumberFormatException i) {
      try {
        Float.parseFloat(commandParam.getRaw());
        return true;
      } catch (NumberFormatException f) {
        try {
          Double.parseDouble(commandParam.getRaw());
          return true;
        } catch (NumberFormatException d) {
          try {
            Long.parseLong(commandParam.getRaw());
            return true;
          } catch (NumberFormatException l) {
            return false;
          }
        }
      }
    }
  }
}
