package com.github.nsilbernagel.discordbot.listeners.impl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.github.nsilbernagel.discordbot.guard.annotations.SpamRegistry;
import com.github.nsilbernagel.discordbot.listeners.AbstractEventListener;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.message.impl.AbstractMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;

@Component
public class MessageCreateEventListener extends AbstractEventListener<MessageCreateEvent> {
  @Value("${app.discord.channels.blacklist:}")
  private BigInteger[] channelBlacklist;

  @Value("${app.guard.spam.enabled:false}")
  private boolean spamProtectionEnabled;

  @Autowired
  private SpamRegistry spamRegistry;

  @Autowired
  private MessageToTaskHandler messageToTaskHandler;

  @Override
  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  @Override
  public void execute(MessageCreateEvent event) {
    Message message = event.getMessage();

    if (!this.canAnswerOnChannel(message.getChannel().block())) {
      return;
    }

    List<AbstractMessageTask> tasks = messageToTaskHandler.getMessageTasks(message);

    if (tasks.size() > 0) {
      this.spamRegistry.countMemberUp(this.messageToTaskHandler.getMsgAuthor());
    }

    tasks.forEach(task -> {
      try {
        task.execute();
      } catch (TaskException taskLogicError) {
        if (taskLogicError.hasMessage()) {
          event.getMessage().getChannel()
              .flatMap(channel -> channel.createMessage(taskLogicError.getMessage())).block();
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
        .filter((channel) -> channelInQuestion.getId().asBigInteger().equals(channel)).findFirst().isEmpty();
  }
}