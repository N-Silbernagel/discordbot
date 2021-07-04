package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.task.Task;
import discord4j.common.util.Snowflake;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

abstract public class MessageDeleteTask extends Task {
  @Getter
  // the messages that a deletion on should trigger the task
  protected final List<MessageInChannel> deletableMessages = new ArrayList<>();

  public void addDeletableMessage(MessageInChannel messageInChannel) {
    this.deletableMessages.add(messageInChannel);
  }

  abstract public void execute(Snowflake channelId, Snowflake messageId);

  public boolean canHandle(MessageInChannel messageInChannel) {
    return this.getDeletableMessages()
        .contains(messageInChannel);
  }
}
