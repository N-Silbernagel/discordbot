package com.github.nsilbernagel.discordbot.schedules;

import com.github.nsilbernagel.discordbot.schedules.dto.ChannelCleaner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;

public class CleanSchedule {

  @Value("${app.discord.channels.exclusive}")
  private String channelIdString;

  @Autowired
  private GatewayDiscordClient discordClient;

  private ChannelCleaner cleaner = new ChannelCleaner();

  @Scheduled(cron = "0 0 0 * * ?")
  public void cleanBotChannel() {
    cleaner.setDiscordClient(discordClient)
        .setChannel(Snowflake.of(channelIdString))
        .removeMessages();
  }

}
