package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.IMessageTask;

import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Message;

@Component
public class PongTask extends AbstractMessageTask implements IMessageTask {
  public final static String KEYWORD = "ping";

  @Override
  public void execute(Message message) {
    this.message = message;
    message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("pong")).subscribe();
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}