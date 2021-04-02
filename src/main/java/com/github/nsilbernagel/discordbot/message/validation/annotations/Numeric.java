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
public @interface Numeric {
  /**
   * the message to return if validation fails
   */
  String value();

  /**
   * a threshold that the numeric value should be bigger than or equal
   */
  long min() default Long.MIN_VALUE;

  /**
   * a threshold that the numeric value should be smaller than or equal
   */
  long max() default Long.MAX_VALUE;
}
