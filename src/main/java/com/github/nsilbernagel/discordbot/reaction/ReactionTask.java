package com.github.nsilbernagel.discordbot.reaction;

import java.util.ArrayList;
import java.util.List;

import com.github.nsilbernagel.discordbot.message.TaskRequest;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;

public abstract class ReactionTask {
  @Getter
  private final List<Message> messages = new ArrayList<>();

  protected final ThreadLocal<TaskRequest> taskRequest = new ThreadLocal<>();

  abstract public boolean canHandle(ReactionEmoji reactionEmoji);

  abstract public ReactionEmoji getTrigger();

  abstract public void action();

  /**
   * Get the message that initiated the reaction task
   */
  protected Message currentMessage() {
    return this.taskRequest.get().getMessage();
  }

  /**
   * Get the channel that the current task was initiated on
   */
  protected TextChannel currentChannel() {
    return this.taskRequest.get().getChannel();
  }

  /**
   * Get the author that initiated the reaction task
   */
  protected Member currentAuthor() {
    return this.taskRequest.get().getAuthor();
  }

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

  public void execute(TaskRequest taskRequest) {
    this.taskRequest.set(taskRequest);

    if (!this.messages.contains(taskRequest.getMessage())) {
      throw new MessageNotReactableException();
    }

    this.action();
  }
}
