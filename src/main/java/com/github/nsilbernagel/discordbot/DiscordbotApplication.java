package com.github.nsilbernagel.discordbot;

import java.util.List;

import com.github.nsilbernagel.discordbot.listeners.AbstractEventListener;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;

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

  @Autowired
  private List<AbstractEventListener<?>> eventListeners;

  @Override
  public void run(String... args) throws Exception {
    if (botToken.length() == 0) {
      throw new MissingTokenException();
    }

    // register event listeners on all classes implementing the IEventListener
    // interface
    eventListeners.forEach((eventListener) -> eventListener.register());

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
