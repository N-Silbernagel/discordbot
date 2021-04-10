package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import discord4j.rest.util.Permission;

@NeedsPermission(Permission.ADMINISTRATOR)
public class AdminMessageTaskStub extends MessageTask {
  @Override
  protected void action(MsgTaskRequest taskRequest) {
  }

  @Override
  public boolean canHandle(String command) {
    return false;
  }
}
