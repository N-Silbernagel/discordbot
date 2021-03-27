package com.github.nsilbernagel.discordbot.guard.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Guard a MessageTask with a needed discord permission
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NeedsPermission {
  discord4j.rest.util.Permission value();
}
