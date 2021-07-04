package com.github.nsilbernagel.discordbot.listener;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.core.env.Environment;

public class FakeListener extends EventListener<MessageCreateEvent>{
  public FakeListener(GatewayDiscordClient discordClient, Environment env) {
    super(discordClient, env);
  }

  @Override
  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  @Override
  public void execute(MessageCreateEvent event) {

  }
}
