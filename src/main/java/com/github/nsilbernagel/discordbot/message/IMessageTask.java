package com.github.nsilbernagel.discordbot.message;

public interface IMessageTask {
  /*
   * Start the task that was triggered by a command in a channel.
   */
  void execute();

  /**
   * Check if a task can do anything with a given command keyword
   *
   * @param keyword
   *                  the keyword to check
   * @return can handle keyword
   */
  public boolean canHandle(String keyword);
}
