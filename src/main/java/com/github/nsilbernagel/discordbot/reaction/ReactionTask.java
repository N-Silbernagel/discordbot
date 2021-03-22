package com.github.nsilbernagel.discordbot.reaction;

import java.util.ArrayList;
import java.util.List;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;

public abstract class ReactionTask {
  @Getter
  private final List<Message> messages = new ArrayList<Message>();

  abstract public boolean canHandle(ReactionEmoji reactionEmoji);

  abstract public ReactionEmoji getTrigger();

  abstract public void action();

  /**
   * Add a message that can be reacted on
   */
  public void addMessage(Message message) {
    message.addReaction(this.getTrigger()).subscribe();
    this.messages.add(message);
  }

  public void execute(Message message) {
    if (!this.messages.contains(message)) {
      throw new MessageNotReactableException();
    }

    this.action();
  }
}
