package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.TestableMono;
import com.github.nsilbernagel.discordbot.message.MessageTestUtil;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.voice.SummonTask;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.object.entity.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayTaskTests {
  @Mock
  private LavaPlayerAudioProvider lavaPlayerAudioProviderMock;
  @Mock
  private AudioPlayerManager audioPlayerManagerMock;
  @Mock
  private SummonTask summonTaskMock;
  @Mock
  private LavaTrackScheduler lavaTrackSchedulerMock;

  private PlayTask playTask;

  private final String requestIdFake = "test";

  private TestableMono<Message> alertMessageMono;

  @BeforeEach
  public void setUp() {
    this.playTask = new PlayTask(this.summonTaskMock, this.lavaPlayerAudioProviderMock, this.lavaTrackSchedulerMock);

    this.alertMessageMono = new TestableMono<>();
  }

  @Test
  public void it_creates_audio_requests_and_registers_them() {
    MsgTaskRequest taskRequest = MessageTestUtil.generateMsgTaskRequest();

    Map<String, AudioRequest> audioRequests = new HashMap<>();

    when(lavaTrackSchedulerMock.getAudioRequest()).thenReturn(audioRequests);

    when(lavaPlayerAudioProviderMock.getPlayerManager()).thenReturn(this.audioPlayerManagerMock);

    // fake successful loading of audio
    doAnswer(invocation -> {
      audioRequests.get(this.requestIdFake).setStatus(AudioStatus.QUEUED);
      return CompletableFuture.completedFuture(null);
    })
        .when(this.audioPlayerManagerMock)
        .loadItem(eq(this.requestIdFake), any(LavaResultHandler.class));

    this.playTask.loadAudioSource(this.requestIdFake, taskRequest);

    assertEquals(1, lavaTrackSchedulerMock.getAudioRequest().size());
  }

  @Test
  public void it_alerts_the_user_when_loading_fails() {
    MsgTaskRequest taskRequest = MessageTestUtil.generateMsgTaskRequest();
    Map<String, AudioRequest> audioRequests = new HashMap<>();

    when(lavaTrackSchedulerMock.getAudioRequest()).thenReturn(audioRequests);

    when(lavaPlayerAudioProviderMock.getPlayerManager()).thenReturn(this.audioPlayerManagerMock);
    when(this.audioPlayerManagerMock.loadItem(eq(this.requestIdFake), any(LavaResultHandler.class))).thenReturn(CompletableFuture.completedFuture(null));

    when(taskRequest.getChannel().createMessage(any(String.class))).thenReturn(this.alertMessageMono.getMono());

    this.playTask.loadAudioSource(this.requestIdFake, taskRequest);

    assertTrue(this.alertMessageMono.wasSubscribedTo());
  }

  @Test
  public void it_deletes_the_audio_request_when_loading_fails() {
    MsgTaskRequest taskRequest = MessageTestUtil.generateMsgTaskRequest();

    Map<String, AudioRequest> audioRequests = new HashMap<>();

    when(lavaTrackSchedulerMock.getAudioRequest()).thenReturn(audioRequests);

    when(lavaPlayerAudioProviderMock.getPlayerManager()).thenReturn(this.audioPlayerManagerMock);
    when(this.audioPlayerManagerMock.loadItem(eq(this.requestIdFake), any(LavaResultHandler.class))).thenReturn(CompletableFuture.completedFuture(null));

    when(taskRequest.getChannel().createMessage(any(String.class))).thenReturn(this.alertMessageMono.getMono());

    this.playTask.loadAudioSource(this.requestIdFake, taskRequest);

    assertEquals(0, lavaTrackSchedulerMock.getAudioRequest().size());
  }
}
