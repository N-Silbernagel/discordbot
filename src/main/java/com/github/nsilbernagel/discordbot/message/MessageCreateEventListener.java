package com.github.nsilbernagel.discordbot.message;

import java.util.ArrayList;
import java.util.List;

import com.github.nsilbernagel.discordbot.message.validation.MessageTaskValidator;
import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.guard.SpamRegistry;
import com.github.nsilbernagel.discordbot.listener.EventListener;
import com.github.nsilbernagel.discordbot.task.TaskException;
import com.github.nsilbernagel.discordbot.task.validation.MessageTaskPreparer;
import com.github.nsilbernagel.discordbot.message.validation.MessageValidationException;

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
  private MessageTaskPreparer messageTaskPreparer;

  @Autowired
  private ChannelBlacklist channelBlacklist;

  @Autowired
  private ExclusiveBotChannel exclusiveBotChannel;

  @Autowired
  private MessageTaskValidator messageTaskValidator;

  @Autowired
  private List<MessageTask<? extends MsgTaskRequest>> tasks;

  private final ThreadLocal<MsgTaskRequest> localMsgTaskRequest = new ThreadLocal<>();

  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  public void execute(MessageCreateEvent event) {
    Message message = event.getMessage();

    // not a command, ignore
    if (!message.getContent().startsWith(commandToken)) {
      return;
    }

    TextChannel channel;
    try {
      channel = (TextChannel) message.getChannel().block();
    } catch (ClassCastException e) {
      // probably using a private channel which we dont support yet
      return;
    }

    MsgTaskRequest taskRequest;
    taskRequest = new MsgTaskRequest(
        message,
        channel,
        message.getAuthorAsMember().block(),
        this.commandToken,
        messageTaskValidator
    );

    this.localMsgTaskRequest.set(taskRequest);

    if (!this.channelBlacklist.canAnswerOnChannel(taskRequest.getChannel())) {
      return;
    }

    if (!this.exclusiveBotChannel.isOnExclusiveChannel(message)) {
      this.exclusiveBotChannel.handleMessageOnOtherChannel(message);
      return;
    }

    List<MessageTask<? extends MsgTaskRequest>> tasks = this.getTasksForCommand(taskRequest.getCommand());

    if (tasks.isEmpty()) {
      // react to members message with question mark emoji to show that the command was not found
      Emoji.QUESTION_MARK.reactOn(message).block();
      return;
    }

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

  private void prepareAndExecuteTask(MessageTask<? extends  MsgTaskRequest> task) throws TaskException {
    try {
      this.messageTaskPreparer.execute(task, this.localMsgTaskRequest.get());
    } catch (MessageValidationException e) {
      throw new TaskException(e.getMessage(), e);
    }

    task.execute(this.localMsgTaskRequest.get());
  }

  /**
   * Get tasks that can handle a given command
   */
  private List<MessageTask<? extends  MsgTaskRequest>> getTasksForCommand(String command) {
    List<MessageTask<? extends  MsgTaskRequest>> result = new ArrayList<>();
    for (MessageTask<? extends  MsgTaskRequest> task : this.tasks) {
      if (task.canHandle(command)) {
        result.add(task);
      }
    }

    return result;
  }

  @Override
  protected void onCheckedException(TaskException exception) {
    if (! exception.hasMessage()) {
      return;
    }

    this.localMsgTaskRequest.get()
        .getChannel()
        .createMessage(exception.getMessage())
        .block();
  }

  @Override
  protected void onUncheckedException(Exception uncheckedException) {
    Emoji.BUG.reactOn(this.localMsgTaskRequest.get().getMessage())
        .block();
  }
}