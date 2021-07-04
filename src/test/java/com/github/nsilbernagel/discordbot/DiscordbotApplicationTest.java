package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.listener.EventListener;
import com.github.nsilbernagel.discordbot.listener.FakeListener;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Mono;
import reactor.test.publisher.PublisherProbe;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DiscordbotApplicationTest {
  @Mock
  private GatewayDiscordClient discordClient;
  @Mock
  private Environment listenerEnv;
  @Mock
  private GatewayDiscordClient listenerDiscordClient;


  private List<EventListener<?>> eventListeners;

  @BeforeEach
  public void setUp() {
    eventListeners = new ArrayList<>();
  }

  @Test
  public void it_fails_if_no_bot_token_is_provided() {
    DiscordbotApplication discordbotApplication = new DiscordbotApplication(discordClient, eventListeners);

    assertThrows(MissingTokenException.class, discordbotApplication::getDiscordClient);
  }

  @Test
  public void it_runs_until_discord_client_disconnects() {
    DiscordbotApplication discordbotApplication = new DiscordbotApplication(discordClient, eventListeners);

    Mono<Void> disconnectMono = mock(Mono.class);
    when(discordClient.onDisconnect()).thenReturn(disconnectMono);

    discordbotApplication.run();

    verify(discordClient.onDisconnect()).block();
  }

  @Test
  public void it_registers_event_listeners() {
    when(discordClient.onDisconnect()).thenReturn(mock(Mono.class));
    FakeListener fakeListener = spy(new FakeListener(listenerDiscordClient, listenerEnv));
    eventListeners.add(fakeListener);

    DiscordbotApplication discordbotApplication = new DiscordbotApplication(discordClient, eventListeners);

    PublisherProbe<MessageCreateEvent> eventFluxProbe = PublisherProbe.empty();

    when(listenerDiscordClient.on(fakeListener.getEventType())).thenReturn(eventFluxProbe.flux());

    discordbotApplication.run();

    verify(fakeListener).register();
  }
}