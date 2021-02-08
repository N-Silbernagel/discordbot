package com.github.nsilbernagel.discordbot.listeners.impl;

import java.util.List;

import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.guard.SpamRegistry;
import com.github.nsilbernagel.discordbot.listeners.AbstractEventListener;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.message.impl.AbstractMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.Getter;

@Component
public class MessageCreateEventListener extends AbstractEventListener<MessageCreateEvent> {
  @Value("${app.guard.spam.enabled:false}")
  private boolean spamProtectionEnabled;

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

  private Message message;

  @Getter
  private TextChannel messageChannel;

  @Override
  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  @Override
  public void execute(MessageCreateEvent event) {
    this.message = event.getMessage();
    this.messageChannel = (TextChannel) this.message.getChannel().block();

    if (!this.channelBlacklist.canAnswerOnChannel(this.messageChannel)) {
      return;
    }

    if (!this.message.getContent().startsWith(commandToken)) {
      return;
    }

    if (!this.exclusiveBotChannel.isOnExclusiveChannel(message)) {
      this.exclusiveBotChannel.handleMessageOnOtherChannel(message);
      return;
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
          this.getMessageChannel()
              .createMessage(taskLogicError.getMessage())
              .block();
        }
      }
    });
  }
}