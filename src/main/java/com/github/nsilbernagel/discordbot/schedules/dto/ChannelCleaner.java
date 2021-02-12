package com.github.nsilbernagel.discordbot.schedules.dto;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

@Component
public class ChannelCleaner {

  @Value("${app.discord.command-token:!}")
  private String commandToken;

  private MessageChannel channel;

  public void execute(MessageChannel channelId) {
    channel.getMessagesBefore(Snowflake.of(Instant.now()))
        .take(50)
        .flatMap(message -> this.deleteIfFromBotOrCommand(message))
        .then()
        .block();
  }

  private Mono<Void> deleteIfFromBotOrCommand(Message message) {
    if (!message.getAuthor().isPresent()) {
      return Mono.empty();
    }

    if (!message.getAuthor().get().isBot() && !message.getContent().startsWith(this.commandToken)) {
      return Mono.empty();
    }

    return message.delete();
  }

}
