package com.github.nsilbernagel.discordbot.message;

public class FalseInputException extends Exception {
    public FalseInputException(String errorMessage) {
        super(errorMessage);
    }
}
