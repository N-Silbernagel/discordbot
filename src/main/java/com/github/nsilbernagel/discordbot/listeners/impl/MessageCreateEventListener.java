package com.github.nsilbernagel.discordbot.listeners.impl;

import java.util.List;

import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.guard.SpamRegistry;
import com.github.nsilbernagel.discordbot.listeners.EventListener;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.MessageTaskPreparer;
import com.github.nsilbernagel.discordbot.validation.MessageValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;

@Component
public class MessageCreateEventListener extends EventListener<MessageCreateEvent> {
  @Value("${app.discord.command-token:!}")
  private String commandToken;

  @Autowired
  private SpamRegistry spamRegistry;

  @Autowired
  private MessageToTaskHandler messageToTaskHandler;

  @Autowired
  private ChannelBlacklist channelBlacklist;

  @Autowired
  private ExclusiveBotChannel exclusiveBotChannel;

  @Autowired
  private MessageTaskPreparer messageTaskPreparer;

  @Getter
  private TextChannel messageChannel;

  @Getter
  private Member msgAuthor;

  @Override
  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  @Override
  public void execute(MessageCreateEvent event) {
    Message message = event.getMessage();
    try {
      this.messageChannel = (TextChannel) message.getChannel().block();
    } catch (ClassCastException e) {
      // probably using a private channel which we dont support yet
      return;
    }

    if (!this.channelBlacklist.canAnswerOnChannel(this.messageChannel)) {
      return;
    }

    if (!message.getContent().startsWith(commandToken)) {
      return;
    }

    if (!this.exclusiveBotChannel.isOnExclusiveChannel(message)) {
      this.exclusiveBotChannel.handleMessageOnOtherChannel(message);
      return;
    }

    List<MessageTask> tasks = messageToTaskHandler.getMessageTasks(message);
    this.msgAuthor = message.getAuthorAsMember().block();

    if (this.spamRegistry.isSpamProtectionEnabled() && this.spamRegistry.memberHasExceededThreshold(this.msgAuthor)) {
      message.addReaction(ReactionEmoji.unicode("👮‍♂️")).subscribe();
      return;
    }

    if (tasks.size() > 0 && this.spamRegistry.isSpamProtectionEnabled()) {
      this.spamRegistry.countMemberUp(this.getMsgAuthor());
    }

    tasks.forEach(task -> {
      try {
        this.prepareAndExecuteTask(task);
      } catch (TaskException taskLogicError) {
        if (taskLogicError.hasMessage()) {
          this.getMessageChannel()
              .createMessage(taskLogicError.getMessage())
              .block();
        }
      }
    });
  }

  private void prepareAndExecuteTask(MessageTask task) throws TaskException {
    try {
      this.messageTaskPreparer.execute(task);
    } catch (MessageValidationException e) {
      throw new TaskException(e.getMessage(), e);
    }

    task.execute();
  }
}