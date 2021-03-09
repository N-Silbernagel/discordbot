package com.github.nsilbernagel.discordbot.reaction;

public class MessageNotReactableException extends ReactionTaskException {
  final static long serialVersionUID = 8L;

  public MessageNotReactableException() {
    super();
  }

  public MessageNotReactableException(String message) {
    super(message);
  }
}
