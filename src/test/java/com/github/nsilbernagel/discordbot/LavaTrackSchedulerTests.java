package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.audio.AudioRequest;
import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.audio.LavaTrackScheduler;
import com.github.nsilbernagel.discordbot.message.TaskRequest;
import com.github.nsilbernagel.discordbot.presence.PresenceManager;
import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.gateway.StatusUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LavaTrackSchedulerTests {
  @Mock
  private LavaPlayerAudioProvider lavaPlayerAudioProviderMock;
  @Mock
  private AudioPlayer audioPlayerMock;
  @Mock
  private GatewayDiscordClient gatewayDiscordClientMock;
  @Mock
  private AudioTrack audioTrackMock;
  @Mock
  private TaskRequest taskRequestMock;
  private PresenceManager presenceManager;

  private final String requestIdFake = "test";

  private AudioRequest audioRequest;
  private LavaTrackScheduler lavaTrackScheduler;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.presenceManager = new PresenceManager(this.gatewayDiscordClientMock);
    this.lavaTrackScheduler = new LavaTrackScheduler(this.lavaPlayerAudioProviderMock, this.presenceManager);

    this.audioRequest = new AudioRequest(
        this.requestIdFake,
        this.taskRequestMock
    );

    lavaTrackScheduler.getAudioRequest()
        .put(this.requestIdFake, audioRequest);

    when(this.lavaPlayerAudioProviderMock.getPlayer()).thenReturn(this.audioPlayerMock);
  }

  @Test
  public void it_immediately_plays_tracks_when_nothing_is_playing() {
    when(this.audioPlayerMock.startTrack(this.audioTrackMock, true)).thenReturn(true);

    lavaTrackScheduler.queue(audioTrackMock, this.requestIdFake);

    assertEquals(0, lavaTrackScheduler.getQueue().size());
  }

  @Test
  public void it_queues_tracks_when_another_track_is_playing() {
    when(this.audioPlayerMock.startTrack(this.audioTrackMock, true)).thenReturn(false);

    lavaTrackScheduler.queue(audioTrackMock, this.requestIdFake);

    assertEquals(1, lavaTrackScheduler.getQueue().size());
  }

  @Test
  public void it_adds_tracks_to_their_corresponding_track_request_when_queueing() {
    when(this.audioPlayerMock.startTrack(this.audioTrackMock, true)).thenReturn(true);

    lavaTrackScheduler.queue(audioTrackMock, this.requestIdFake);

    AudioTrack firstTrackInRequestTrackList = lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .get(0);

    assertEquals(this.audioTrackMock, firstTrackInRequestTrackList);
  }

  @Test
  public void it_removes_track_from_its_corresponding_track_request_when_track_ends() {
    AudioTrack secondTrackMock = mock(AudioTrack.class);

    lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(secondTrackMock);

    lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    when(this.gatewayDiscordClientMock.updatePresence(any(StatusUpdate.class))).thenReturn(Mono.empty());

    lavaTrackScheduler.onTrackEnd(this.audioPlayerMock, this.audioTrackMock, AudioTrackEndReason.STOPPED);

    List<AudioTrack> resultingAudioTrackList = lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList();


    assertEquals(1, resultingAudioTrackList.size());
  }

  @Test
  public void it_removes_audio_request_when_track_ends_and_track_list_is_empty() {
    lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    when(this.gatewayDiscordClientMock.updatePresence(any(StatusUpdate.class))).thenReturn(Mono.empty());

    lavaTrackScheduler.onTrackEnd(this.audioPlayerMock, this.audioTrackMock, AudioTrackEndReason.STOPPED);

    assertNull(lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake));
  }

  @Test
  public void it_alerts_when_playing_track_went_wrong() {
    lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    TextChannel textChannelMock = mock(TextChannel.class);

    when(this.taskRequestMock.getChannel()).thenReturn(textChannelMock);
    Mono<Message> alertMessageMonoMock = mock(Mono.class);
    when(textChannelMock.createMessage(any(String.class))).thenReturn(alertMessageMonoMock);

    this.lavaTrackScheduler.onTrackException(this.audioPlayerMock, this.audioTrackMock, mock(FriendlyException.class));

    verify(alertMessageMonoMock).block();
  }

  @Test
  public void it_sets_the_presence_to_playing_when_a_track_starts() {
    this.lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    String trackTitleTestValue = "test";

    AudioTrackInfo audioTrackInfoMock = mock(AudioTrackInfo.class);
    ReflectionTestUtils.setField(audioTrackInfoMock, "title", trackTitleTestValue);
    when(this.audioTrackMock.getInfo()).thenReturn(audioTrackInfoMock);

    Mono<Void> updatePresenceMock = mock(Mono.class);
    ArgumentCaptor<StatusUpdate> statusUpdateArgumentCaptor = ArgumentCaptor.forClass(StatusUpdate.class);
    when(this.gatewayDiscordClientMock.updatePresence(statusUpdateArgumentCaptor.capture())).thenReturn(updatePresenceMock);

    this.lavaTrackScheduler.onTrackStart(this.audioPlayerMock, this.audioTrackMock);

    verify(updatePresenceMock).subscribe();

    assertEquals(trackTitleTestValue, statusUpdateArgumentCaptor.getValue().game().get().name());
  }

  @Test
  public void it_sets_the_presence_to_online_when_next_track_should_start_on_empty_queue() {
    this.lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    Mono<Void> updatePresenceMock = mock(Mono.class);
    ArgumentCaptor<StatusUpdate> statusUpdateArgumentCaptor = ArgumentCaptor.forClass(StatusUpdate.class);
    when(this.gatewayDiscordClientMock.updatePresence(statusUpdateArgumentCaptor.capture())).thenReturn(updatePresenceMock);

    this.lavaTrackScheduler.nextTrack();

    verify(updatePresenceMock).subscribe();

    assertTrue(statusUpdateArgumentCaptor.getValue().game().isEmpty());
    assertEquals("online", statusUpdateArgumentCaptor.getValue().status().toLowerCase(Locale.ROOT));
  }

  @Test
  public void it_sets_the_presence_to_online_when_track_ends_and_the_next_should_not_start() {
    this.lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    Mono<Void> updatePresenceMock = mock(Mono.class);
    ArgumentCaptor<StatusUpdate> statusUpdateArgumentCaptor = ArgumentCaptor.forClass(StatusUpdate.class);
    when(this.gatewayDiscordClientMock.updatePresence(statusUpdateArgumentCaptor.capture())).thenReturn(updatePresenceMock);

    this.lavaTrackScheduler.onTrackEnd(this.audioPlayerMock, this.audioTrackMock, AudioTrackEndReason.CLEANUP);

    verify(updatePresenceMock).subscribe();

    assertTrue(statusUpdateArgumentCaptor.getValue().game().isEmpty());
    assertEquals("online", statusUpdateArgumentCaptor.getValue().status().toLowerCase(Locale.ROOT));
  }

  @Test
  public void it_indicates_a_paused_track_through_the_activity_containing_pause_emoji() {
    this.lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    String trackTitleTestValue = "test";

    AudioTrackInfo audioTrackInfoMock = mock(AudioTrackInfo.class);
    ReflectionTestUtils.setField(audioTrackInfoMock, "title", trackTitleTestValue);
    when(this.audioTrackMock.getInfo()).thenReturn(audioTrackInfoMock);

    ReflectionTestUtils.setField(this.presenceManager, "currentlyPlaying", this.audioTrackMock.getInfo().title);

    Mono<Void> updatePresenceMock = mock(Mono.class);
    ArgumentCaptor<StatusUpdate> statusUpdateArgumentCaptor = ArgumentCaptor.forClass(StatusUpdate.class);
    when(this.gatewayDiscordClientMock.updatePresence(statusUpdateArgumentCaptor.capture())).thenReturn(updatePresenceMock);

    this.lavaTrackScheduler.onPlayerPause(this.audioPlayerMock);

    verify(updatePresenceMock).subscribe();

    assertTrue(
        statusUpdateArgumentCaptor.getValue()
            .game().get()
            .name()
            .contains(Emoji.PAUSE.getUnicodeEmoji().toString())
    );
  }

  @Test
  public void it_sets_the_presence_to_playing_when_the_player_resumes() {
    this.lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);


    String trackTitleTestValue = "test";

    AudioTrackInfo audioTrackInfoMock = mock(AudioTrackInfo.class);
    ReflectionTestUtils.setField(audioTrackInfoMock, "title", trackTitleTestValue);
    when(this.audioTrackMock.getInfo()).thenReturn(audioTrackInfoMock);

    ReflectionTestUtils.setField(this.presenceManager, "currentlyPlaying", this.audioTrackMock.getInfo().title);

    Mono<Void> updatePresenceMock = mock(Mono.class);
    ArgumentCaptor<StatusUpdate> statusUpdateArgumentCaptor = ArgumentCaptor.forClass(StatusUpdate.class);
    when(this.gatewayDiscordClientMock.updatePresence(statusUpdateArgumentCaptor.capture())).thenReturn(updatePresenceMock);

    this.lavaTrackScheduler.onPlayerResume(this.audioPlayerMock);

    verify(updatePresenceMock).subscribe();

    assertEquals(trackTitleTestValue, statusUpdateArgumentCaptor.getValue().game().get().name());
  }
}
