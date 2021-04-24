package com.github.nsilbernagel.discordbot.interaction;

import com.github.nsilbernagel.discordbot.listener.EventListener;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.discordjson.json.ApplicationCommandInteractionData;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InteractionCreateEventListener extends EventListener<InteractionCreateEvent> {
  private final List<InteractionTask> interactionTasks;

  public InteractionCreateEventListener(GatewayDiscordClient discordClient, Environment env, List<InteractionTask> interactionTasks) {
    super(discordClient, env);
    this.interactionTasks = interactionTasks;
  }

  @Override
  public Class<InteractionCreateEvent> getEventType() {
    return InteractionCreateEvent.class;
  }

  @Override
  public void execute(InteractionCreateEvent event) {
    Optional<ApplicationCommandInteractionData> interactionData = event.getInteraction()
        .getData()
        .data().toOptional();

    if(interactionData.isEmpty()){
      return;
    }

    InteractionTaskRequest request = InteractionTaskRequest.fromEvent(event);

    this.interactionTasks.forEach(interactionTask -> {
      if (interactionTask.canHandle(request.getCommandName())) {
        event.acknowledgeEphemeral();
        interactionTask.execute(request);
      }
    });
  }
}
