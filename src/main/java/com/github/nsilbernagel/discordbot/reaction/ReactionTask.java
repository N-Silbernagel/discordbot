package com.github.nsilbernagel.discordbot.reaction;

import java.util.ArrayList;
import java.util.List;

import com.github.nsilbernagel.discordbot.task.Task;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;

public abstract class ReactionTask extends Task {
  @Getter
  private final List<Message> messages = new ArrayList<>();

  abstract public boolean canHandle(ReactionEmoji reactionEmoji);

  abstract public ReactionEmoji getTrigger();

  abstract public void action(ReactionTaskRequest taskRequest);

  /**
   * Add a message that can be reacted on
   */
  public void addMessage(Message message) {
    message.addReaction(this.getTrigger()).subscribe();
    this.messages.add(message);
  }

  /**
   * remove a message from the list of reactable messages
   */
  public void removeMessage(Message message) {
    this.messages.remove(message);
  }

  public void execute(ReactionTaskRequest taskRequest) {
    if (!this.messages.contains(taskRequest.getMessage())) {
      throw new MessageNotReactableException();
    }

    this.action(taskRequest);
  }
}
