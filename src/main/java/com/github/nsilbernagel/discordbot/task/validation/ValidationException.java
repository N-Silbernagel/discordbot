package com.github.nsilbernagel.discordbot.task.validation;

public class ValidationException extends RuntimeException{
  public ValidationException(String message){
    super(message);
  }
}
