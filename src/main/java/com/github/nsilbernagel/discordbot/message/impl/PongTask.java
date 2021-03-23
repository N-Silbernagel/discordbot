package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.MessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PongTask extends MessageTask {
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