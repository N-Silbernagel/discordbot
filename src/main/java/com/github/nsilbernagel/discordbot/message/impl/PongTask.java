package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.IMessageTask;

import org.springframework.stereotype.Component;

@Component
public class PongTask extends AbstractMessageTask implements IMessageTask {
  public final static String KEYWORD = "ping";

  @Override
  public void execute() {
    this.getMessage()
        .getChannel()
        .flatMap(messageChannel -> messageChannel.createMessage("pong"))
        .block();
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}