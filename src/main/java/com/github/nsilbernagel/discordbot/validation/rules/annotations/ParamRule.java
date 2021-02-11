package com.github.nsilbernagel.discordbot.validation.rules.annotations;

public @interface ParamRule {
  /**
   * the message to return if validation fails
   */
  String value();
}
