package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

abstract public class AbstractMessageTask {
  @Autowired
  protected MessageToTaskHandler messageToTaskHandler;

  protected Message getMessage() {
    return this.messageToTaskHandler.getMessage();
  }

  /**
   * Answer the message with a given text on the same channel
   *
   * @param answerText
   */
  public Mono<Message> answerMessage(String answerText) {
    return this.getMessage()
        .getChannel()
        .flatMap(messageChannel -> messageChannel.createMessage(answerText));
  }

  public void execute() {
    this.action();
  }

  /*
   * Start the task that was triggered by a command in a channel.
   */
  abstract protected void action();

  /**
   * Check if a task can do anything with a given command keyword
   *
   * @param keyword
   *                  the keyword to check
   * @return can handle keyword
   */
  abstract public boolean canHandle(String keyword);
}
