package com.github.nsilbernagel.discordbot.message;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageDeleteEventListenerTest {
  @Mock
  private Environment env;
  @Mock
  private GatewayDiscordClient discordClient;
  @Mock
  private MessageDeleteTask messageDeleteTask;
  @Mock
  private MessageDeleteEvent messageDeleteEvent;

  private MessageDeleteEventListener messageDeleteEventListener;

  private final Snowflake deletableMessageId = Snowflake.of(1);
  private final Snowflake deletableMessageChannelId = Snowflake.of(1);
  private final MessageInChannel messageInChannel = new MessageInChannel(
      this.deletableMessageChannelId,
      this.deletableMessageId
  );

  private final List<MessageDeleteTask> messageDeleteTasks = new ArrayList<>();

  @BeforeEach
  public void setUp() {
    this.messageDeleteTasks.add(this.messageDeleteTask);

    this.messageDeleteEventListener = new MessageDeleteEventListener(this.discordClient, this.env, this.messageDeleteTasks);
  }

  @Test
  public void it_executes_the_correct_task_for_a_deleted_message () {
    when(this.messageDeleteEvent.getChannelId()).thenReturn(this.deletableMessageChannelId);
    when(this.messageDeleteEvent.getMessageId()).thenReturn(this.deletableMessageId);

    when(this.messageDeleteTask.canHandle(eq(this.messageInChannel))).thenReturn(true);

    this.messageDeleteEventListener.execute(this.messageDeleteEvent);

    verify(this.messageDeleteTask).execute(this.deletableMessageChannelId, this.deletableMessageId);
  }
}