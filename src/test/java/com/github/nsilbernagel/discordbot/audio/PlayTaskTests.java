package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.audio.*;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.TaskRequest;
import com.github.nsilbernagel.discordbot.voice.SummonTask;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    this.playTask = new PlayTask(this.summonTaskMock, this.lavaPlayerAudioProviderMock, this.lavaTrackSchedulerMock);
  }

  @Test
  public void it_creates_audio_requests_and_registers_them() {
    MsgTaskRequest taskRequest = new MsgTaskRequest(
        Mockito.mock(Message.class),
        Mockito.mock(TextChannel.class),
        Mockito.mock(Member.class)
    );

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

    when(taskRequest.getChannel().createMessage(any(String.class))).thenReturn(Mono.empty());

    this.playTask.loadAudioSource(this.requestIdFake, taskRequest);

    assertEquals(1, lavaTrackSchedulerMock.getAudioRequest().size());
  }

  @Test
  public void it_alerts_the_user_when_loading_fails() {
    MsgTaskRequest taskRequest = new MsgTaskRequest(
        Mockito.mock(Message.class),
        Mockito.mock(TextChannel.class),
        Mockito.mock(Member.class)
    );

    Map<String, AudioRequest> audioRequests = new HashMap<>();

    when(lavaTrackSchedulerMock.getAudioRequest()).thenReturn(audioRequests);

    when(lavaPlayerAudioProviderMock.getPlayerManager()).thenReturn(this.audioPlayerManagerMock);
    when(this.audioPlayerManagerMock.loadItem(eq(this.requestIdFake), any(LavaResultHandler.class))).thenReturn(CompletableFuture.completedFuture(null));

    Mono<Message> alertMessageMonoMock = mock(Mono.class);

    when(taskRequest.getChannel().createMessage(any(String.class))).thenReturn(alertMessageMonoMock);

    this.playTask.loadAudioSource(this.requestIdFake, taskRequest);

    verify(alertMessageMonoMock).block();
  }

  @Test
  public void it_deletes_the_audio_request_when_loading_fails() {
    MsgTaskRequest taskRequest = new MsgTaskRequest(
        Mockito.mock(Message.class),
        Mockito.mock(TextChannel.class),
        Mockito.mock(Member.class)
    );

    Map<String, AudioRequest> audioRequests = new HashMap<>();

    when(lavaTrackSchedulerMock.getAudioRequest()).thenReturn(audioRequests);

    when(lavaPlayerAudioProviderMock.getPlayerManager()).thenReturn(this.audioPlayerManagerMock);
    when(this.audioPlayerManagerMock.loadItem(eq(this.requestIdFake), any(LavaResultHandler.class))).thenReturn(CompletableFuture.completedFuture(null));

    Mono<Message> alertMessageMonoMock = mock(Mono.class);

    when(taskRequest.getChannel().createMessage(any(String.class))).thenReturn(alertMessageMonoMock);

    this.playTask.loadAudioSource(this.requestIdFake, taskRequest);

    assertEquals(0, lavaTrackSchedulerMock.getAudioRequest().size());
  }
}
