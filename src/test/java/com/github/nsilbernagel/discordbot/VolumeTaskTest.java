package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.audio.VolumeTask;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord4j.core.object.entity.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class VolumeTaskTest {
  @Mock
  private LavaPlayerAudioProvider lavaPlayerAudioProviderMock;
  @Mock
  private AudioPlayer audioPlayerMock;
  private VolumeTask volumeTask;

  @BeforeEach
  public void SetUp() {
    MockitoAnnotations.initMocks(this);

    this.volumeTask = spy(new VolumeTask(this.lavaPlayerAudioProviderMock));
  }

  @Test
  public void it_returns_the_current_volume_if_no_volume_param_was_specified(){
    Integer fakeVolume = 50;

    // current volume fake
    when(this.lavaPlayerAudioProviderMock.getPlayer()).thenReturn(this.audioPlayerMock);
    when(this.audioPlayerMock.getVolume()).thenReturn(fakeVolume);

    ArgumentCaptor<String> volumeMessageResponseCaptor = ArgumentCaptor.forClass(String.class);
    Mono<Message> volumeMessageMono = mock(Mono.class);

    doReturn(volumeMessageMono).when(this.volumeTask).answerMessage(volumeMessageResponseCaptor.capture());

    this.volumeTask.action();

    verify(volumeMessageMono).block();
    assertTrue(volumeMessageResponseCaptor.getValue().contains(fakeVolume.toString()));
  }
}
