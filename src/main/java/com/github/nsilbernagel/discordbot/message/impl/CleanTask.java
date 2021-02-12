package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.maintainance.ChannelCleaner;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.rest.util.Permission;

@Component
@NeedsPermission(Permission.BAN_MEMBERS)
public class CleanTask extends AbstractMessageTask {

  public static final String KEYWORD = "clean";

  @Autowired
  private ChannelCleaner channelCleaner;

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    this.channelCleaner
        .execute(this.messageCreateEventListener.getMessageChannel());
  }

}