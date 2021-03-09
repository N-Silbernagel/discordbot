package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PongTask extends AbstractMessageTask {
  public final static String KEYWORD = "ping";

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  @Override
  public void action() {
    this.answerMessage("pong");
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}