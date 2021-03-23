package com.github.nsilbernagel.discordbot.message;

import java.util.List;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.guard.SpamRegistry;
import com.github.nsilbernagel.discordbot.listener.EventListener;
import com.github.nsilbernagel.discordbot.validation.MessageTaskPreparer;
import com.github.nsilbernagel.discordbot.validation.MessageValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
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

  @Getter
  private Message message;

  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  public void execute(MessageCreateEvent event) {
    this.message = event.getMessage();
    try {
      this.messageChannel = (TextChannel) this.message.getChannel().block();
    } catch (ClassCastException e) {
      // probably using a private channel which we dont support yet
      return;
    }

    if (!this.channelBlacklist.canAnswerOnChannel(this.messageChannel)) {
      return;
    }

    if (!this.message.getContent().startsWith(commandToken)) {
      return;
    }

    if (!this.exclusiveBotChannel.isOnExclusiveChannel(this.message)) {
      this.exclusiveBotChannel.handleMessageOnOtherChannel(this.message);
      return;
    }

    List<MessageTask> tasks = messageToTaskHandler.getMessageTasks(this.message);
    this.msgAuthor = this.message.getAuthorAsMember().block();

    if (this.spamRegistry.isSpamProtectionEnabled() && this.spamRegistry.memberHasExceededThreshold(this.msgAuthor)) {
      Emoji.GUARD.reactOn(this.message).subscribe();
      return;
    }

    if (tasks.size() > 0 && this.spamRegistry.isSpamProtectionEnabled()) {
      this.spamRegistry.countMemberUp(this.getMsgAuthor());
    }

    tasks.forEach(this::prepareAndExecuteTask);
  }

  private void prepareAndExecuteTask(MessageTask task) throws TaskException {
    try {
      this.messageTaskPreparer.execute(task);
    } catch (MessageValidationException e) {
      throw new TaskException(e.getMessage(), e);
    }

    task.execute();
  }

  protected void onCheckedException(TaskException exception) {
    if (exception.hasMessage()) {
      this.getMessageChannel()
          .createMessage(exception.getMessage())
          .block();
    }
  }

  protected void onUncheckedException(Exception uncheckedException) {
    Emoji.BUG.reactOn(this.message).subscribe();
  }
}