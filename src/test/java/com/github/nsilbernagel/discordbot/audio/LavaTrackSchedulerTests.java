package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.TestableMono;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
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
  private MsgTaskRequest taskRequestMock;
  @Mock
  private AudioTrackInfo audioTrackInfoMock;
  private PresenceManager presenceManager;

  private final String requestIdFake = "test";
  private TestableMono<Void> updatePresence;

  private LavaTrackScheduler lavaTrackScheduler;

  private final ArgumentCaptor<StatusUpdate> statusUpdateArgumentCaptor = ArgumentCaptor.forClass(StatusUpdate.class);

  @BeforeEach
  public void setUp() {
    this.presenceManager = new PresenceManager(this.gatewayDiscordClientMock);
    this.lavaTrackScheduler = new LavaTrackScheduler(this.lavaPlayerAudioProviderMock, this.presenceManager);

    AudioRequest audioRequest = new AudioRequest(
        this.requestIdFake,
        this.taskRequestMock
    );

    this.updatePresence = new TestableMono<>();

    lavaTrackScheduler.getAudioRequest()
        .put(this.requestIdFake, audioRequest);

    lenient().when(this.lavaPlayerAudioProviderMock.getPlayer()).thenReturn(this.audioPlayerMock);
    lenient().when(this.gatewayDiscordClientMock.updatePresence(statusUpdateArgumentCaptor.capture())).thenReturn(this.updatePresence.getMono());

    lenient().when(this.audioTrackMock.getInfo()).thenReturn(audioTrackInfoMock);
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
        .addAll(Arrays.asList(secondTrackMock, this.audioTrackMock));

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
    TestableMono<Message> alertMessageMono = new TestableMono<>();
    when(textChannelMock.createMessage(any(String.class))).thenReturn(alertMessageMono.getMono());

    this.lavaTrackScheduler.onTrackException(this.audioPlayerMock, this.audioTrackMock, mock(FriendlyException.class));

    assertTrue(alertMessageMono.wasSubscribedTo());
  }

  @Test
  public void it_sets_the_presence_to_playing_when_a_track_starts() {
    this.lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    String trackTitleTestValue = "test";

    ReflectionTestUtils.setField(audioTrackInfoMock, "title", trackTitleTestValue);

    this.lavaTrackScheduler.onTrackStart(this.audioPlayerMock, this.audioTrackMock);

    assertTrue(this.updatePresence.wasSubscribedTo());

    assertEquals(trackTitleTestValue, statusUpdateArgumentCaptor.getValue().game().get().name());
  }

  @Test
  public void it_sets_the_presence_to_online_when_next_track_should_start_on_empty_queue() {
    this.lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    this.lavaTrackScheduler.nextTrack();

    assertTrue(this.updatePresence.wasSubscribedTo());

    assertTrue(statusUpdateArgumentCaptor.getValue().game().isEmpty());
    assertEquals("online", statusUpdateArgumentCaptor.getValue().status().toLowerCase(Locale.ROOT));
  }

  @Test
  public void it_sets_the_presence_to_online_when_track_ends_and_the_next_should_not_start() {
    this.lavaTrackScheduler.getAudioRequest()
        .get(this.requestIdFake)
        .getTrackList()
        .add(this.audioTrackMock);

    this.lavaTrackScheduler.onTrackEnd(this.audioPlayerMock, this.audioTrackMock, AudioTrackEndReason.CLEANUP);

    assertTrue(this.updatePresence.wasSubscribedTo());

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

    ReflectionTestUtils.setField(audioTrackInfoMock, "title", trackTitleTestValue);

    ReflectionTestUtils.setField(this.presenceManager, "currentlyPlaying", this.audioTrackMock.getInfo().title);

    this.lavaTrackScheduler.onPlayerPause(this.audioPlayerMock);

    assertTrue(this.updatePresence.wasSubscribedTo());

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

    ReflectionTestUtils.setField(audioTrackInfoMock, "title", trackTitleTestValue);

    ReflectionTestUtils.setField(this.presenceManager, "currentlyPlaying", this.audioTrackMock.getInfo().title);

    this.lavaTrackScheduler.onPlayerResume(this.audioPlayerMock);

    assertTrue(this.updatePresence.wasSubscribedTo());

    assertEquals(trackTitleTestValue, statusUpdateArgumentCaptor.getValue().game().get().name());
  }
}
