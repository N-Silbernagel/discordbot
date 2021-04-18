package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.listener.EventListener;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MessageDeleteEventListener extends EventListener<MessageDeleteEvent> {
  private final List<MessageDeleteTask> messageDeleteTasks;

  public MessageDeleteEventListener(GatewayDiscordClient discordClient, Environment env, List<MessageDeleteTask> messageDeleteTasks) {
    super(discordClient, env);

    this.messageDeleteTasks = messageDeleteTasks;
  }

  @Override
  public Class<MessageDeleteEvent> getEventType() {
    return MessageDeleteEvent.class;
  }

  @Override
  public void execute(MessageDeleteEvent event) {
    this.executeFittingDeleteTasks(event.getChannelId(), event.getMessageId());
  }

  public void executeFittingDeleteTasks(Snowflake channelId, Snowflake messageId) {
    Stream<MessageDeleteTask> fittingMessageDeleteTasks = this.messageDeleteTasks.stream()
        .filter(messageDeleteTask -> messageDeleteTask.canHandle(new MessageInChannel(
            channelId,
            messageId
        )));

    fittingMessageDeleteTasks.forEach(messageDeleteTask -> messageDeleteTask.execute(channelId, messageId));
  }
}
