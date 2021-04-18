package com.github.nsilbernagel.discordbot.message;

import discord4j.common.util.Snowflake;
import lombok.Data;

@Data
public class MessageInChannel {
  private final Snowflake channelId;
  private final Snowflake messageId;
}
