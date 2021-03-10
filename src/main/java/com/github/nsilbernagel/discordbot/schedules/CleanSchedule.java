package com.github.nsilbernagel.discordbot.schedules;

import javax.annotation.PostConstruct;

import com.github.nsilbernagel.discordbot.maintainance.ChannelCleaner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;

public class CleanSchedule {

  @Value("${app.discord.channels.exclusive}")
  private Snowflake channelIdString;

  @Autowired
  private GatewayDiscordClient discordClient;

  @Autowired
  private ChannelCleaner channelCleaner;

  private TextChannel channelToClean;

  @PostConstruct
  private void fetchChannelToClean() {
    try {
      this.channelToClean = (TextChannel) this.discordClient.getChannelById(this.channelIdString)
          .block();
    } catch (Throwable e) {
      throw new RuntimeException("Textchannel configured under prop app.discord.channels.exclusive could not be found.",
          e);
    }
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void cleanBotChannel() {
    this.channelCleaner.execute(channelToClean);
  }

}
