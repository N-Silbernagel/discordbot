package com.github.nsilbernagel.discordbot.maintainance;

import com.github.nsilbernagel.discordbot.guard.ExclusiveChannelEntity;
import com.github.nsilbernagel.discordbot.guard.ExclusiveChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class CleanSchedule {
  private final GatewayDiscordClient discordClient;
  private final ChannelCleaner channelCleaner;
  private final ExclusiveChannelRepository exclusiveChannelRepo;

  public CleanSchedule(GatewayDiscordClient discordClient, ChannelCleaner channelCleaner, ExclusiveChannelRepository exclusiveChannelRepo) {
    this.discordClient = discordClient;
    this.channelCleaner = channelCleaner;
    this.exclusiveChannelRepo = exclusiveChannelRepo;
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void cleanBotChannels() {
    exclusiveChannelRepo.findAll().forEach(this::cleanChannel);
  }


  private void cleanChannel(ExclusiveChannelEntity channel) {
    Snowflake exclusiveChannelId = Snowflake.of(channel.getChannelId());
    TextChannel exclusiveChannel = (TextChannel) discordClient.getChannelById(exclusiveChannelId).block();
    assert exclusiveChannel != null;

    this.channelCleaner.execute(exclusiveChannel);
  }

}
