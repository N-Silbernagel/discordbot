package com.github.nsilbernagel.discordbot.listener;

import com.github.nsilbernagel.discordbot.task.TaskException;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.gateway.ShardInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import reactor.test.publisher.PublisherProbe;
import reactor.test.publisher.TestPublisher;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventListenerTest {
  @Mock
  private GatewayDiscordClient discordClientMock;
  @Mock
  private Environment envMock;
  @Mock
  private ShardInfo shardInfo;
  @Mock
  private Message message;
  @Mock
  private Member member;

  private final Snowflake guildId = Snowflake.of(1);

  private PublisherProbe<MessageCreateEvent> onEventFluxProbe;

  @BeforeEach
  public void setUp() {
    this.onEventFluxProbe = PublisherProbe.empty();
  }

  @Test
  public void it_registers_itself_on_the_discord_client() {
    EventListener<MessageCreateEvent> eventListener = new FakeListener(discordClientMock, envMock);
    when(this.discordClientMock.on(eq(eventListener.getEventType())))
        .thenReturn(onEventFluxProbe.flux());

    eventListener.register();

    onEventFluxProbe.wasSubscribed();
  }

  @Test
  public void a_discord_event_triggers_the_execute_method() {
    EventListener<MessageCreateEvent> eventListener = spy(new FakeListener(discordClientMock, envMock));

    TestPublisher<MessageCreateEvent> eventTestPublisher = TestPublisher.create();

    MessageCreateEvent fakeMessageCreateEvent = new MessageCreateEvent(
        discordClientMock,
        shardInfo,
        message,
        guildId.asLong(),
        member
    );

    when(this.discordClientMock.on(eventListener.getEventType()))
        .thenReturn(eventTestPublisher.flux());

    eventListener.register();

    eventTestPublisher.next(fakeMessageCreateEvent);
    eventTestPublisher.complete();

    verify(eventListener).execute(fakeMessageCreateEvent);
  }
}