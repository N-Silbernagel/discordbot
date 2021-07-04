package com.github.nsilbernagel.discordbot.message;

import discord4j.common.util.Snowflake;

public record MessageInChannel(Snowflake channelId, Snowflake messageId) {
}
