package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.interaction.InteractionTask;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import org.springframework.stereotype.Component;

@Component
public class VotekickInteractionTask extends InteractionTask {

  @Override
  public void action(InteractionTaskRequest request) {
    request.getEvent().replyEphemeral("Stay by, slash commands are coming!").block();
  }

  @Override
  public boolean canHandle(String command) {
    return command.equals("votekick");
  }
}
