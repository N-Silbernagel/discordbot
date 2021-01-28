package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.IMessageTask;
import discord4j.core.object.entity.Message;

public class NilsTask extends AbstractMessageTask implements IMessageTask {
  private final static String KEYWORD = "meinung";

  public NilsTask(Message message) {
    super(message);
  }

  @Override
  public void execute() {
    message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("Richtiger Lappen.")).subscribe();
  }

  public static String getKeyword() {
    return KEYWORD;
  }
}
