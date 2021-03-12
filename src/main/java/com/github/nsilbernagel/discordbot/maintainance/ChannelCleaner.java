package com.github.nsilbernagel.discordbot.maintainance;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ChannelCleaner {

  @Value("${app.discord.command-token:!}")
  private String commandToken;

  Flux<Message> messagesToDelete;

  public void execute(TextChannel channel) {
    Flux<Message> messagesToDelete = channel.getMessagesBefore(Snowflake.of(Instant.now()))
        .take(50)
        .filter(this::checkIfFromBotOrCommand);

    channel.bulkDeleteMessages(messagesToDelete)
        .blockLast();
  }

  private Boolean checkIfFromBotOrCommand(Message message){
    if (message.getAuthor().isEmpty()) {
      return false;
    }

    return message.getAuthor().get().isBot() || message.getContent().startsWith(this.commandToken);
  }
}
