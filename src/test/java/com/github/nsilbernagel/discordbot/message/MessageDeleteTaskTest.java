package com.github.nsilbernagel.discordbot.message;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageDeleteTaskTest {
  private MessageDeleteTask messageDeleteTask;

  @BeforeEach
  public void setUp() {
    this.messageDeleteTask = spy(new MessageDeleteTask() {
      @Override
      public void execute(Snowflake channelId, Snowflake messageId) {

      }
    });
  }

  @Test
  public void deletable_messages_can_be_registered() {
    MessageInChannel messageInChannel = new MessageInChannel(Snowflake.of(1), Snowflake.of(1));

    messageDeleteTask.addDeletableMessage(messageInChannel);

    assertTrue(messageDeleteTask.getDeletableMessages().contains(messageInChannel));
  }

  @Test
  public void it_knows_if_it_can_handle_the_deletion_of_a_message() {
    MessageInChannel messageInChannel = new MessageInChannel(Snowflake.of(1), Snowflake.of(1));

    messageDeleteTask.addDeletableMessage(messageInChannel);

    assertTrue(messageDeleteTask.canHandle(new MessageInChannel(
        Snowflake.of(1),
        Snowflake.of(1)
    )));
  }

  @Test
  public void it_knows_if_it_cannot_handle_the_deletion_of_a_message() {
    MessageInChannel messageInChannel = new MessageInChannel(Snowflake.of(1), Snowflake.of(1));

    messageDeleteTask.addDeletableMessage(messageInChannel);

    assertFalse(messageDeleteTask.canHandle(new MessageInChannel(
        Snowflake.of(1),
        Snowflake.of(2)
    )));
  }
}