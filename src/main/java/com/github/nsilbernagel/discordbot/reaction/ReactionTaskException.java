package com.github.nsilbernagel.discordbot.reaction;

public class ReactionTaskException extends RuntimeException {
  final static long serialVersionUID = 7L;

  public ReactionTaskException() {
    super();
  }

  public ReactionTaskException(String message) {
    super(message);
  }
}
