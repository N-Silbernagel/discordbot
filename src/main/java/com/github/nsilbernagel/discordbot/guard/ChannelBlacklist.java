package com.github.nsilbernagel.discordbot.guard;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.Channel;

@Component
public class ChannelBlacklist {

  @Value("${app.discord.channels.blacklist:}")
  private Snowflake[] channelBlacklist;

  /**
   * Check if bot should answer on the message's channel as per the
   * app.discord.channels.blacklist property
   *
   * @param channelInQuestion
   *                            the channel of the current message
   */
  public boolean canAnswerOnChannel(Channel channelInQuestion) {
    return Arrays.stream(this.channelBlacklist)
        .filter(channel -> channel.equals(channelInQuestion.getId()))
        .findFirst()
        .isEmpty();
  }
}
