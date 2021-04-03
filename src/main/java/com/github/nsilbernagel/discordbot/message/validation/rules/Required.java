package com.github.nsilbernagel.discordbot.message.validation.rules;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.task.validation.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class Required extends ValidationRule {
  @Override
  public boolean validate(CommandParam commandParam) {
    return commandParam.getRaw() != null;
  }
}
