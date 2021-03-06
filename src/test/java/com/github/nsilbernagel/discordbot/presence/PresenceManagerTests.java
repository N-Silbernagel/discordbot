package com.github.nsilbernagel.discordbot.presence;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.gateway.StatusUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.publisher.PublisherProbe;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PresenceManagerTests {
  private PresenceManager presenceManager;

  @Mock
  private GatewayDiscordClient discordClientMock;
  @Captor
  private ArgumentCaptor<StatusUpdate> statusUpdateArgumentCaptor;

  private PublisherProbe<Void> updatePresenceProbe;
  private final String testPlayingString = "test";

  @BeforeEach
  public void setUp() {
    this.presenceManager = new PresenceManager(this.discordClientMock);

    this.updatePresenceProbe = PublisherProbe.empty();

    when(this.discordClientMock.updatePresence(any(StatusUpdate.class))).thenReturn(this.updatePresenceProbe.mono());
  }

  @Test
  public void it_can_set_clients_presence_to_online_doing_nothing() {
    this.presenceManager.online().block();

    verify(this.discordClientMock).updatePresence(this.statusUpdateArgumentCaptor.capture());
    updatePresenceProbe.wasSubscribed();

    assertEquals("online", this.statusUpdateArgumentCaptor.getValue().status().toLowerCase());
    assertTrue(this.statusUpdateArgumentCaptor.getValue().game().isEmpty());
  }

  @Test
  public void it_can_set_clients_presence_to_playing_track() {
    this.presenceManager.trackPlaying(this.testPlayingString).block();

    verify(this.discordClientMock).updatePresence(this.statusUpdateArgumentCaptor.capture());
    updatePresenceProbe.wasSubscribed();

    assertEquals("online", this.statusUpdateArgumentCaptor.getValue().status().toLowerCase());
    assertEquals(this.testPlayingString, this.statusUpdateArgumentCaptor.getValue().game().get().name());
  }

  @Test
  public void it_can_set_clients_presence_to_paused_track() {
    ReflectionTestUtils.setField(this.presenceManager, "currentlyPlaying", this.testPlayingString);

    this.presenceManager.trackPaused().block();

    verify(this.discordClientMock).updatePresence(this.statusUpdateArgumentCaptor.capture());
    updatePresenceProbe.wasSubscribed();

    assertEquals("online", this.statusUpdateArgumentCaptor.getValue().status().toLowerCase());
    assertTrue(this.statusUpdateArgumentCaptor.getValue().game().get().name().contains(Emoji.PAUSE.getUnicodeEmoji().toString()));
    assertTrue(this.statusUpdateArgumentCaptor.getValue().game().get().name().contains(this.testPlayingString));
  }

  @Test
  public void it_can_resume_last_playing_state() {
    ReflectionTestUtils.setField(this.presenceManager, "currentlyPlaying", this.testPlayingString);

    this.presenceManager.trackResumed().block();

    verify(this.discordClientMock).updatePresence(this.statusUpdateArgumentCaptor.capture());
    updatePresenceProbe.wasSubscribed();

    assertEquals("online", this.statusUpdateArgumentCaptor.getValue().status().toLowerCase());
    assertFalse(this.statusUpdateArgumentCaptor.getValue().game().get().name().contains(Emoji.PAUSE.getUnicodeEmoji().toString()));
    assertTrue(this.statusUpdateArgumentCaptor.getValue().game().get().name().contains(this.testPlayingString));
  }
}
