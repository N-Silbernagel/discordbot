package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.IMessageTask;

import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Message;

@Component
public class NilsTask extends AbstractMessageTask implements IMessageTask {
  private final static String KEYWORD = "meinung";

  @Override
  public void execute(Message message) {
    this.message = message;
    message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("Richtiger Lappen.")).subscribe();
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}
