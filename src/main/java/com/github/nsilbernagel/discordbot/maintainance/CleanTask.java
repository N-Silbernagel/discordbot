package com.github.nsilbernagel.discordbot.maintainance;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.message.MessageTask;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.rest.util.Permission;

@Component
@NeedsPermission(Permission.BAN_MEMBERS)
public class CleanTask extends MessageTask {

  public static final String KEYWORD = "clean";

  @Autowired
  private ChannelCleaner channelCleaner;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action(MsgTaskRequest taskRequest) {
    this.channelCleaner.execute(taskRequest.getChannel());
  }

}