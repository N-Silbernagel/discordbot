package com.github.nsilbernagel.discordbot.interaction;

import com.github.nsilbernagel.discordbot.listener.EventListener;
import com.github.nsilbernagel.discordbot.task.TaskException;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.discordjson.json.ApplicationCommandInteractionData;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InteractionCreateEventListener extends EventListener<InteractionCreateEvent> {
  private List<InteractionTask> interactionTasks;

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

    this.interactionTasks.forEach(interactionTask ->
        this.executeTaskThatCanHandleCommandName(interactionTask, event)
    );
  }

  private void executeTaskThatCanHandleCommandName(InteractionTask task, InteractionCreateEvent event) {
    if (task.canHandle(event.getCommandName())){
      task.execute(event);
    }
  }

  @Override
  protected void onCheckedException(TaskException checkedException) {
    // don't handle checked exceptions, discord will tell them something went wrong
  }

  @Override
  protected void onUncheckedException(Exception uncheckedException) {
    // don't handle unchecked exceptions, discord will tell them something went wrong
  }
}
