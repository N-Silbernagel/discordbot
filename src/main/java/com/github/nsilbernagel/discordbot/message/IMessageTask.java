package com.github.nsilbernagel.discordbot.message;

import discord4j.core.object.entity.Message;

public interface IMessageTask {
  /*
   * Start the task that was triggered by a command in a channel.
   */
  void execute(Message message);

  public boolean canHandle(String keyword);
}
