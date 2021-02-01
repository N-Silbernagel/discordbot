package com.github.nsilbernagel.discordbot.message.impl;

import discord4j.core.object.entity.Message;

abstract class AbstractMessageTask {
  public Message message;

  /**
   * Answer the message with a given text on the same channel
   *
   * @param answerText
   */
  public void answerMessage(String answerText) {
    message.getChannel().flatMap(messageChannel -> messageChannel.createMessage(answerText)).subscribe();
  }
}
