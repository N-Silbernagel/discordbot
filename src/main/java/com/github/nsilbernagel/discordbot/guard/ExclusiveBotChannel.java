package com.github.nsilbernagel.discordbot.guard;

import discord4j.core.object.entity.User;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * A Bot can be exclusively available on a certain text channel. This can be
 * configured through properties
 */
@Component
final public class ExclusiveBotChannel {
  private final GatewayDiscordClient discordClient;
  private final ExclusiveChannelRepository exclusiveChannelRepository;

  public ExclusiveBotChannel(ExclusiveChannelRepository exclusiveChannelRepository, GatewayDiscordClient discordClient) {
    this.exclusiveChannelRepository = exclusiveChannelRepository;
    this.discordClient = discordClient;
  }

  /**
   * Check if a given message was sent on the channel the bot is exclusively
   * active on
   */
  public boolean isOnExclusiveChannel(Message message) {
    if(message.getGuildId().isEmpty()){
      return true;
    }

    Optional<ExclusiveChannelEntity> exclusiveChannel = this.exclusiveChannelRepository.findByguildId(message.getGuildId().get().asLong());

    if (exclusiveChannel.isEmpty()) {
      return true;
    }

    this.discordClient.getChannelById(Snowflake.of(exclusiveChannel.get().getChannelId()))
        .switchIfEmpty(Mono.error(new ChannelNotFoundException()))
        .block();

    return message.getChannelId().asLong() == exclusiveChannel.get().getChannelId();
  }

  /**
   * Handle a message written on a Textchannel that is not the bots exclusive
   * channel
   *
   * @param message the message that was sent on the text channel that is not the bot's exclusive one
   */
  public void handleMessageOnOtherChannel(Message message) {
    // delete message
    message.delete().block();
    // tell user privately to use exclusive channel
    message.getAuthor().ifPresent(user -> this.sendPrivateMessage(user, message));
  }

  private void sendPrivateMessage(User user, Message message) {
    long exclusiveChannelId = this.exclusiveChannelRepository.findByguildId(message.getGuildId().get().asLong())
        .get()
        .getChannelId();
    user.getPrivateChannel()
        .block()
        .createMessage(msg -> msg.setContent("Ich bin nur über den Textchannel <#" + exclusiveChannelId + "> erreichbar."))
        .block();
  }
}
