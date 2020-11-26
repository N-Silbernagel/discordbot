package com.github.nsilbernagel.discordbot.message;

public interface IMessageTask {
  /*
   * Start the task that was triggered by a command in a channel.
   */
  void execute();
}
