package com.github.nsilbernagel.discordbot.reaction;

import java.util.List;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;

public abstract class AbstractReactionTask {
  @Getter
  private List<Message> messages;

  abstract public boolean canHandle(ReactionEmoji reactionEmoji);

  abstract public void action();

  public boolean addMessage(Message message) {
    return this.messages.add(message);
  }

  public void execute(Message message) {
    if (!this.messages.contains(message)) {
      throw new MessageNotReactableException();
    }

    this.action();
  }
}
