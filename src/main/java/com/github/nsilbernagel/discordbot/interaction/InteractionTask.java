package com.github.nsilbernagel.discordbot.interaction;

import com.github.nsilbernagel.discordbot.task.Task;
import discord4j.core.event.domain.InteractionCreateEvent;

abstract public class InteractionTask extends Task {
  public void execute(InteractionCreateEvent event){
    this.action(event);
  }

  abstract public void action(InteractionCreateEvent event);

  abstract public boolean canHandle(String command);
}
