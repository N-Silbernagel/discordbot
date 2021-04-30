package com.github.nsilbernagel.discordbot.interaction;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import discord4j.rest.util.Permission;

@NeedsPermission(Permission.ADMINISTRATOR)
public class AdminInteractionTaskStub extends InteractionTask {

  @Override
  public void action(InteractionTaskRequest request) {

  }

  @Override
  public boolean canHandle(String command) {
    return false;
  }
}
