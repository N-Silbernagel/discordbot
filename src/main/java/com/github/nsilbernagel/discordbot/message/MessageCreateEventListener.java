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
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

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

  private final ThreadLocal<TaskRequest> localMsgTaskRequest = new ThreadLocal<>();

  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  public void execute(MessageCreateEvent event) {
    Message message = event.getMessage();
    TaskRequest taskRequest;
    try {
      taskRequest = new TaskRequest(
          message,
          (TextChannel) message.getChannel().block(),
          message.getAuthorAsMember().block()
      );
    } catch (ClassCastException e) {
      // probably using a private channel which we dont support yet
      return;
    }

    this.localMsgTaskRequest.set(taskRequest);

    if (!this.channelBlacklist.canAnswerOnChannel(taskRequest.getChannel())) {
      return;
    }

    if (!taskRequest.getMessage().getContent().startsWith(commandToken)) {
      return;
    }

    if (!this.exclusiveBotChannel.isOnExclusiveChannel(message)) {
      this.exclusiveBotChannel.handleMessageOnOtherChannel(message);
      return;
    }

    List<MessageTask> tasks = messageToTaskHandler.getMessageTasks(message);

    if(this.spamRegistry.isEnabled()){
      if (this.spamRegistry.memberHasExceededThreshold(taskRequest.getAuthor())) {
        Emoji.GUARD.reactOn(message).subscribe();
        return;
      }

      if (tasks.size() > 0) {
        this.spamRegistry.countMemberUp(taskRequest.getAuthor());
      }
    }

    tasks.forEach(this::prepareAndExecuteTask);
  }

  private void prepareAndExecuteTask(MessageTask task) throws TaskException {
    try {
      this.messageTaskPreparer.execute(task);
    } catch (MessageValidationException e) {
      throw new TaskException(e.getMessage(), e);
    }

    task.execute(this.localMsgTaskRequest.get());
  }

  protected void onCheckedException(TaskException exception) {
    if (! exception.hasMessage()) {
      return;
    }

    this.localMsgTaskRequest.get()
        .getChannel()
        .createMessage(exception.getMessage())
        .block();
  }

  protected void onUncheckedException(Exception uncheckedException) {
    Emoji.BUG.reactOn(this.localMsgTaskRequest.get().getMessage())
        .block();
  }
}