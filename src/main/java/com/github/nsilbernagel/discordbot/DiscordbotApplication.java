package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.listeners.EventListener;
import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DiscordbotApplication implements CommandLineRunner {
  @Value("${app.discord.token:}")
  private String botToken;

  @Autowired
  private GatewayDiscordClient discordClient;

  @Override
  public void run(String... args) throws Exception {
    if (botToken.length() == 0) {
      throw new MissingTokenException();
    }

    EventListener<MessageCreateEvent> listener = new MessageCreateEventListener();
    this.discordClient.on(listener.getEventType()).subscribe(listener::execute);

    this.discordClient.onDisconnect().block();
  }

  public static void main(String[] args) {
    SpringApplication.run(DiscordbotApplication.class, args);
  }

  @Bean
  public GatewayDiscordClient getDiscordClient() {
    return DiscordClient.create(this.botToken).login().block();
  }
}
