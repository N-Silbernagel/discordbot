package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.listeners.EventListener;
import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscordbotApplication implements CommandLineRunner {
  @Value("${app.discord.token:}")
  private String botToken;

  @Override
  public void run(String... args) throws Exception {
    if (botToken.length() == 0) {
      throw new MissingTokenException();
    }

    DiscordClient dClient = DiscordClient.create(botToken);
    GatewayDiscordClient client = dClient.login().block();
    EventListener<MessageCreateEvent> listener = new MessageCreateEventListener();
    client.on(listener.getEventType()).subscribe(listener::execute);

    client.onDisconnect().block();
  }

  public static void main(String[] args) {
    SpringApplication.run(DiscordbotApplication.class, args);
  }
}
