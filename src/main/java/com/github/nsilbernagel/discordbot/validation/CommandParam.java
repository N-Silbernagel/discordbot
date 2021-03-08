package com.github.nsilbernagel.discordbot.validation;

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
   * TODO: allow for input of range for command param to be able to extend over
   * spaces
   * 
   * @return
   */
  int range() default 1;
}
