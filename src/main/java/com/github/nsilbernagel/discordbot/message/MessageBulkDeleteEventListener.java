package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.listener.EventListener;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageBulkDeleteEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MessageBulkDeleteEventListener extends EventListener<MessageBulkDeleteEvent> {
  private final MessageDeleteEventListener messageDeleteEventListener;

  public MessageBulkDeleteEventListener(GatewayDiscordClient discordClient, Environment env, MessageDeleteEventListener messageDeleteEventListener) {
    super(discordClient, env);

    this.messageDeleteEventListener = messageDeleteEventListener;
  }

  @Override
  public Class<MessageBulkDeleteEvent> getEventType() {
    return MessageBulkDeleteEvent.class;
  }

  @Override
  public void execute(MessageBulkDeleteEvent event) {
    event.getMessageIds()
        .forEach(messageId -> this.messageDeleteEventListener.executeFittingDeleteTasks(event.getChannelId(), messageId));
  }
}
