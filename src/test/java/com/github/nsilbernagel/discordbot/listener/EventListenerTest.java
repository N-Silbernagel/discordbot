package com.github.nsilbernagel.discordbot.listener;

import com.github.nsilbernagel.discordbot.TestableFlux;
import com.github.nsilbernagel.discordbot.task.TaskException;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EventListenerTest {
  @Mock
  private GatewayDiscordClient discordClientMock;
  @Mock
  private Environment envMock;

  private TestableFlux<MessageCreateEvent> onEventFlux;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    this.onEventFlux = new TestableFlux<>();
  }

  @Test
  public void it_registers_itself_on_the_discord_client() {
    when(this.discordClientMock.on(eq(MessageCreateEvent.class))).thenReturn(this.onEventFlux.getFlux());

    EventListener<MessageCreateEvent> eventListener = this.eventListenerStub();

    eventListener.register();

    assertTrue(this.onEventFlux.wasSubscribedTo());
  }

  private EventListener<MessageCreateEvent> eventListenerStub() {
    return new EventListener<>(this.discordClientMock, this.envMock) {
      @Override
      public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
      }

      @Override
      public void execute(MessageCreateEvent event) {
      }

      @Override
      protected void onCheckedException(TaskException checkedException) {
      }

      @Override
      protected void onUncheckedException(Exception uncheckedException) {
      }
    };
  }
}