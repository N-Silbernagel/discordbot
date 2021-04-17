package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.interaction.InteractionTask;
import discord4j.core.event.domain.InteractionCreateEvent;
import org.springframework.stereotype.Component;

@Component
public class VotekickInteractionTask extends InteractionTask {

  @Override
  public void action(InteractionCreateEvent event) {
    event.replyEphemeral("Stay by, slash commands are coming!").block();
  }

  @Override
  public boolean canHandle(String command) {
    return command.equals("votekick");
  }
}
