package com.github.nsilbernagel.discordbot;

import java.util.List;

import com.github.nsilbernagel.discordbot.listener.EventListener;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.DependsOn;

@EnableScheduling
@SpringBootApplication
public class DiscordbotApplication implements CommandLineRunner {
  @Value("${app.discord.token:}")
  @Getter
  private String botToken;

  private final GatewayDiscordClient discordClient;

  private final List<EventListener<?>> eventListeners;

  public DiscordbotApplication(GatewayDiscordClient discordClient, List<EventListener<?>> eventListeners) {
    this.discordClient = discordClient;
    this.eventListeners = eventListeners;
  }

  @Override
  public void run(String... args) {
    // register event listeners on all classes extending the
    // EventListener class
    eventListeners.forEach(EventListener::register);

    this.discordClient.onDisconnect().block();
  }

  public static void main(String[] args) {
    SpringApplication.run(DiscordbotApplication.class, args);
  }

  @Bean
  @DependsOn("LavaPlayerAudioProvider")
  public GatewayDiscordClient getDiscordClient() {
    if (getBotToken() == null || getBotToken().length() == 0) {
      throw new MissingTokenException();
    }

    return DiscordClient.create(this.getBotToken())
        .login()
        .block();
  }
}
