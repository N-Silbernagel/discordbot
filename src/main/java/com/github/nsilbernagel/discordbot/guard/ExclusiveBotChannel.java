package com.github.nsilbernagel.discordbot.guard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.Getter;

/**
 * A Bot can be exclusively available on a certain text channel. This can be
 * configured through properties
 */
@Component
final public class ExclusiveBotChannel {
  /**
   * An ID of a text channel that the bot should soley act on
   */
  @Value("${app.discord.channels.exclusive:}")
  private String exclusiveBotChannelIdString;

  @Autowired
  private GatewayDiscordClient gatewayDiscordClient;

  @Getter
  private TextChannel exclusiveBotChannel;

  @Getter
  private boolean exclusiveBotChannelConfigured;

  /**
   * bootstrap exclusive bot channel guard from config values
   */
  @PostConstruct
  public void execute() {
    this.exclusiveBotChannelConfigured = !StringUtils.isEmpty(this.exclusiveBotChannelIdString);

    if (!this.exclusiveBotChannelConfigured) {
      return;
    }

    Snowflake exclusiveBotChannelId = Snowflake.of(this.exclusiveBotChannelIdString);

    try {
      this.exclusiveBotChannel = (TextChannel) this.gatewayDiscordClient.getChannelById(exclusiveBotChannelId)
          .block();
    } catch (Throwable e) {
      throw new RuntimeException("Textchannel configured under prop app.discord.channels.exclusive could not be found.",
          e);
    }
  }

  /**
   * Check if a given message was sent on the channel the bot is exclusively
   * active on
   */
  public boolean isOnExclusiveChannel(Message message) {
    if (!this.exclusiveBotChannelConfigured) {
      return true;
    }

    return message.getChannelId().equals(this.exclusiveBotChannel.getId());
  }

  /**
   * Handle a message written on a Textchannel that is not the bots exclusive
   * channel
   * 
   * @param message
   *                  the message that was sent on the text channel that is not
   *                  the bot's exclusive one
   */
  public void handleMessageOnOtherChannel(Message message) {
    // delete message
    message.delete().subscribe();
    // tell user privately to use exclusive channel
    message.getAuthor()
        .get()
        .getPrivateChannel()
        .block()
        .createMessage(msg -> msg
            .setContent(
                "Ich bin nur über den Textchannel <#" + this.exclusiveBotChannelIdString + "> erreichbar."))
        .block();
  }
}
