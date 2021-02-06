package com.github.nsilbernagel.discordbot.schedules.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.github.nsilbernagel.discordbot.schedules.dtos.CustomTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.VoiceChannel;

@Component
public class ChannelNameClock {

  @Value("${app.discord.channels.rename:}")
  private String channelId;

  @Autowired
  private GatewayDiscordClient discordClient;

  private VoiceChannel channel;

  @PostConstruct
  public void initialize() {
    if (this.channelId.equals("")) {
      return;
    }
    this.channel = (VoiceChannel) discordClient.getChannelById(Snowflake.of(channelId)).block();
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
    if (this.channelId.equals("")) {
      return;
    }
    this.channel.edit(spec -> spec.setName("Stammtisch")).block();
  }

}
