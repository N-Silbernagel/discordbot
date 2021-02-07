package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

abstract class AbstractMessageTask {
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
}
