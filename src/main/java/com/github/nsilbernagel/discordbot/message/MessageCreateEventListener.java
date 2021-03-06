package com.github.nsilbernagel.discordbot.message;

import java.util.ArrayList;
import java.util.List;

import com.github.nsilbernagel.discordbot.guard.MessageFilter;
import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.guard.SpamRegistry;
import com.github.nsilbernagel.discordbot.listener.EventListener;
import com.github.nsilbernagel.discordbot.task.TaskException;

import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

@Component
public class MessageCreateEventListener extends EventListener<MessageCreateEvent> {
  @Value("${app.discord.command-token:!}")
  private String commandToken;

  private final SpamRegistry spamRegistry;
  private final ExclusiveBotChannel exclusiveBotChannel;
  private final List<MessageCreateTask> tasks;
  private final MessageFilter messageFilter;

  private final ThreadLocal<MsgTaskRequest> localMsgTaskRequest = new ThreadLocal<>();

  public MessageCreateEventListener(
      SpamRegistry spamRegistry,
      ExclusiveBotChannel exclusiveBotChannel,
      List<MessageCreateTask> tasks,
      GatewayDiscordClient discordClient,
      Environment env,
      MessageFilter messageFilter
  ) {
    super(discordClient, env);
    this.spamRegistry = spamRegistry;
    this.exclusiveBotChannel = exclusiveBotChannel;
    this.tasks = tasks;
    this.messageFilter = messageFilter;
  }

  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  public void execute(MessageCreateEvent event) {
    Message message = event.getMessage();

    this.messageFilter.execute(message);

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

    MsgTaskRequest taskRequest = new MsgTaskRequest(
        message,
        channel,
        message.getAuthorAsMember().block(),
        this.commandToken
    );

    this.localMsgTaskRequest.set(taskRequest);

    if (!this.exclusiveBotChannel.isOnExclusiveChannel(message)) {
      this.exclusiveBotChannel.handleMessageOnOtherChannel(message);
      return;
    }

    List<MessageCreateTask> tasks = this.getTasksForCommand(taskRequest.getCommand());

    if (tasks.isEmpty()) {
      // react to members message with question mark emoji to show that the command was not found
      Emoji.QUESTION_MARK.reactOn(message).block();
      return;
    }

    if(this.spamRegistry.isEnabled()){
      if (this.spamRegistry.memberHasExceededThreshold(taskRequest.getAuthor())) {
        Emoji.GUARD.reactOn(message).block();
        return;
      }

      this.spamRegistry.countMemberUp(taskRequest.getAuthor());
    }

    tasks.forEach(task ->
        task.execute(taskRequest)
    );
  }

  /**
   * Get tasks that can handle a given command
   */
  private List<MessageCreateTask> getTasksForCommand(String command) {
    List<MessageCreateTask> result = new ArrayList<>();
    for (MessageCreateTask task : this.tasks) {
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