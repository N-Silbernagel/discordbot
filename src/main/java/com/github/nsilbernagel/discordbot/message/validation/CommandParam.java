package com.github.nsilbernagel.discordbot.message.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CommandParam {
  /**
   * the position of the command param in the chain of words given after a bot
   * command in a msg
   */
  int pos();

  /**
   * Take all command param from pos to pos + the range. Integer.MAX_VALUE is
   * recommended for infinity
   */
  int range() default 1;
}
