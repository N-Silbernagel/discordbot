package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.schedules.dto.ChannelCleaner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;

@Component
public class CleanTask extends AbstractMessageTask {

  public static final String KEYWORD = "clean";

  private ChannelCleaner cleaner = new ChannelCleaner();

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;
  @Autowired
  private GatewayDiscordClient discordClient;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    cleaner.setDiscordClient(discordClient)
            .setChannel(this.messageCreateEventListener.getMessageChannel().getId())
            .removeMessages();
  }

}