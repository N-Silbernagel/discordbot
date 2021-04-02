package com.github.nsilbernagel.discordbot.message.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Task class' field is a command param and is required
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
  /**
   * the message to return if validation fails
   */
  String value();
}
