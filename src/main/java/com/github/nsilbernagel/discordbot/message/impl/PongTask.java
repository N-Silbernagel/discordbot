package com.github.nsilbernagel.discordbot.message.impl;

import org.springframework.stereotype.Component;

@Component
public class PongTask extends AbstractMessageTask {
  public final static String KEYWORD = "ping";

  @Override
  public void action() {
    this.getMessage()
        .getChannel()
        .flatMap(messageChannel -> messageChannel.createMessage("pong"))
        .block();
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}