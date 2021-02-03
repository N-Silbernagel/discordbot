package com.github.nsilbernagel.discordbot.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;

@Component
public abstract class AbstractEventListener<T extends Event> {
  @Autowired
  private GatewayDiscordClient discordClient;

  /**
   * Get type of the discord4j event the listener listens for
   */
  abstract public Class<T> getEventType();

  /**
   * The action to perform when the target event was fired
   *
   * @param event
   */
  abstract public void execute(T event);

  /**
   * Register a D4J Event listener
   */
  public void register() {
    this.discordClient.on(this.getEventType()).subscribe(this::execute);
  }
}
