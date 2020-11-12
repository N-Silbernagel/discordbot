package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import com.github.nsilbernagel.discordbot.message.impl.PongTask;
import discord4j.core.object.entity.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

public class MessageToTaskHandlerTest {
  @Test
  public void getMessageTask_shallRetrievePong() {
    Message pingMessage = Mockito.mock(Message.class);
    Mockito.when(pingMessage.getContent()).thenReturn("!ping");
    Optional<IMessageTask> actual = MessageToTaskHandler.getMessageTask(pingMessage);
    if (actual.isPresent()) {
      if (actual.get() instanceof PongTask) {
        assertTrue(true);
      } else {
        fail();
      }
    } else {
      fail();
    }
  }
}
