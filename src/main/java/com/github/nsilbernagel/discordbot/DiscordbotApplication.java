package com.github.nsilbernagel.discordbot;

import java.util.List;

import com.github.nsilbernagel.discordbot.listener.EventListener;
import com.github.nsilbernagel.discordbot.schedules.ChannelNameClock;
import com.github.nsilbernagel.discordbot.maintainance.CleanSchedule;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.DependsOn;

@EnableScheduling
@SpringBootApplication
public class DiscordbotApplication implements CommandLineRunner {
  @Value("${app.discord.token:}")
  private String botToken;

  @Autowired
  private GatewayDiscordClient discordClient;

  @Autowired
  private List<EventListener<?>> eventListeners;

  @Override
  public void run(String... args) throws Exception {
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
    if (botToken.length() == 0) {
      throw new MissingTokenException();
    }

    return DiscordClient.create(this.botToken)
        .login()
        .block();
  }

  @Bean
  @ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${app.discord.channels.rename:}')")
  public ChannelNameClock channelNameClock() {
    return new ChannelNameClock();
  }

  @Bean
  @ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${app.discord.channels.exclusive:}')")
  public CleanSchedule cleanSchedule() {
    return new CleanSchedule();
  }
}
