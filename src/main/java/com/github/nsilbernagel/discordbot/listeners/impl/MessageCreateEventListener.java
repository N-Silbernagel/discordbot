package com.github.nsilbernagel.discordbot.listeners.impl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.github.nsilbernagel.discordbot.guard.SpamRegistry;
import com.github.nsilbernagel.discordbot.listeners.AbstractEventListener;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.message.impl.AbstractMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;

@Component
public class MessageCreateEventListener extends AbstractEventListener<MessageCreateEvent> {
  @Value("${app.discord.channels.blacklist:}")
  private BigInteger[] channelBlacklist;

  @Value("${app.guard.spam.enabled:false}")
  private boolean spamProtectionEnabled;

  @Value("${app.discord.channels.exclusive:}")
  /**
   * An ID of a text channel that the bot should soley act on
   */
  private String exclusiveBotChannelIdString;

  @Value("${app.discord.command-token:!}")
  private String commandToken;

  @Autowired
  private SpamRegistry spamRegistry;

  @Autowired
  private GatewayDiscordClient gatewayDiscordClient;

  @Autowired
  private MessageToTaskHandler messageToTaskHandler;

  private Message message;

  @Override
  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  @Override
  public void execute(MessageCreateEvent event) {
    this.message = event.getMessage();

    if (!this.canAnswerOnChannel(this.message.getChannel().block())) {
      return;
    }

    if (!this.message.getContent().startsWith(commandToken)) {
      return;
    }

    if (!StringUtils.isEmpty(this.exclusiveBotChannelIdString)) {
      boolean messageComingFromExclusiveBotChannel = this.handleExclusiveBotChannel();
      if (!messageComingFromExclusiveBotChannel) {
        return;
      }
    }

    List<AbstractMessageTask> tasks = messageToTaskHandler.getMessageTasks(this.message);

    if (tasks.size() > 0) {
      this.spamRegistry.countMemberUp(this.messageToTaskHandler.getMsgAuthor());
    }

    tasks.forEach(task -> {
      try {
        task.execute();
      } catch (TaskException taskLogicError) {
        if (taskLogicError.hasMessage()) {
          event.getMessage().getChannel()
              .flatMap(channel -> channel.createMessage(taskLogicError.getMessage()))
              .block();
        }
      }
    });
  }

  /**
   * Check if bot should answer on the message's channel as per the
   * app.discord.channels.blacklist property
   *
   * @param channelInQuestion
   *                            the channel of the current message
   */
  private boolean canAnswerOnChannel(Channel channelInQuestion) {
    return Arrays.stream(this.channelBlacklist)
        .filter((channel) -> channelInQuestion.getId()
            .asBigInteger()
            .equals(channel))
        .findFirst()
        .isEmpty();
  }

  private boolean handleExclusiveBotChannel() {
    Snowflake exclusiveBotChannelId = Snowflake.of(this.exclusiveBotChannelIdString);

    TextChannel exclusiveChannel;
    try {
      exclusiveChannel = (TextChannel) this.gatewayDiscordClient.getChannelById(exclusiveBotChannelId)
          .block();
    } catch (Throwable e) {
      throw new RuntimeException("Textchannel configured under prop app.discord.channels.exclusive could not be found.",
          e);
    }

    boolean isExclusiveChannel = this.message.getChannelId().equals(exclusiveBotChannelId);
    if (isExclusiveChannel) {
      return true;
    }
    // delete message
    this.message.delete().subscribe();
    // tell user privatly to use exclusive channel
    this.message.getAuthor()
        .get()
        .getPrivateChannel()
        .block()
        .createMessage(msg -> msg
            .setContent("Bitte schreibe nur über den Textchannel '" + exclusiveChannel.getName() + "' mit mir."))
        .block();

    return false;
  }
}