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

@Component
public class ExclusiveBotChannel {
  @Value("${app.discord.channels.exclusive:}")
  /**
   * An ID of a text channel that the bot should soley act on
   */
  private String exclusiveBotChannelIdString;

  @Autowired
  private GatewayDiscordClient gatewayDiscordClient;

  @Getter
  private TextChannel exclusiveBotChannel;

  @Getter
  private boolean exclusiveBotChannelConfigured;

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

  public boolean isOnExclusiveChannel(Message message) {
    if (!this.exclusiveBotChannelConfigured) {
      return true;
    }

    return message.getChannelId().equals(this.exclusiveBotChannel.getId());
  }

  public void handleMessageOnOtherChannel(Message message) {
    // delete message
    message.delete().subscribe();
    // tell user privatly to use exclusive channel
    message.getAuthor()
        .get()
        .getPrivateChannel()
        .block()
        .createMessage(msg -> msg
            .setContent(
                "Bitte schreibe nur über den Textchannel '" + this.exclusiveBotChannel.getName() + "' mit mir."))
        .block();
  }
}
