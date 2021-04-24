package com.github.nsilbernagel.discordbot.interaction;

import com.github.nsilbernagel.discordbot.task.Task;
import discord4j.core.event.domain.InteractionCreateEvent;

abstract public class InteractionTask extends Task {
  public void execute(InteractionTaskRequest request){
    this.action(request);
  }

  abstract public void action(InteractionTaskRequest request);

  abstract public boolean canHandle(String command);
}
