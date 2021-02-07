package com.github.nsilbernagel.discordbot.guard.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * Guard a MessageTask with a needed discord permission
 */
public @interface NeedsPermission {
  discord4j.rest.util.Permission value();
}
