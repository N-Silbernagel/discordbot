package com.github.nsilbernagel.discordbot.other;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class ChannelNameClock {
  public final static String DEFAULT_NAME = "Stammtisch";
  public final static String MORNING_NAME = "Morgenrunde";
  public final static String NOON_NAME = "Mittagsrunde";
  public final static String EVENING_NAME = "Abendrunde";

  public final static int MORNING_BEGIN = 6;
  public final static int NOON_BEGIN = 12;
  public final static int EVENING_BEGIN = 18;

  @Value("${app.discord.channels.rename}")
  private Snowflake channelId;

  private final GatewayDiscordClient discordClient;

  private VoiceChannel channel;

  public ChannelNameClock(GatewayDiscordClient discordClient) {
    this.discordClient = discordClient;
  }

  @PostConstruct
  public void initialize() {
    this.channel = (VoiceChannel) discordClient.getChannelById(channelId).block();
    this.changeChannelName();
  }

  @Scheduled(cron = "0 0 */1 * * ?")
  public void changeChannelName() {
    int hour = this.getHourOfDay();

    if (hour >= MORNING_BEGIN && hour < NOON_BEGIN) {
      this.channel.edit(spec -> spec.setName(hourString(hour) + " |  " + MORNING_NAME)).block();
    } else if (hour >= NOON_BEGIN && hour < EVENING_BEGIN) {
      this.channel.edit(spec -> spec.setName(hourString(hour) + " |  " + NOON_NAME)).block();
    } else {
      this.channel.edit(spec -> spec.setName(hourString(hour) + " |  " + EVENING_NAME)).block();
    }
  }

  public int getHourOfDay() {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
  }

  private static String hourString(int hourOfDay) {
    return hourOfDay + " Uhr";
  }

  @PreDestroy
  public void shutdown() {
    this.channel.edit(spec -> spec.setName(DEFAULT_NAME)).block();
  }

}
