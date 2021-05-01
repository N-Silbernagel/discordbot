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

@Component
public class ChannelNameClock {

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
    CustomTime time = new CustomTime();

    if (time.getHour() >= 6 && time.getHour() < 12) {
      this.channel.edit(spec -> spec.setName(time.getString() + " |  Morgenrunde")).block();
    } else if (time.getHour() >= 12 && time.getHour() < 18) {
      this.channel.edit(spec -> spec.setName(time.getString() + " |  Mittagsrunde")).block();
    } else {
      this.channel.edit(spec -> spec.setName(time.getString() + " |  Abendrunde")).block();
    }
  }

  @PreDestroy
  public void shutdown() {
    this.channel.edit(spec -> spec.setName("Stammtisch")).block();
  }

}
