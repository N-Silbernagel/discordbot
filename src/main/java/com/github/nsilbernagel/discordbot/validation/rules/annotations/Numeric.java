package com.github.nsilbernagel.discordbot.validation.rules.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * The Task class' field is a command param and is required
 */
public @interface Numeric {
  /**
   * the message to return if validation fails
   */
  String value();
}
