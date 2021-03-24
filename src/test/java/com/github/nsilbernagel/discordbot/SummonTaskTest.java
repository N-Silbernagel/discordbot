package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.voice.LeaveTask;
import com.github.nsilbernagel.discordbot.voice.SummonTask;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.VoiceConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public class SummonTaskTest {
  @Mock
  private Member memberMock;
  @Mock
  private VoiceState voiceStateMock;
  @Mock
  private MessageCreateEventListener messageCreateEventListenerMock;
  @Mock
  private LavaPlayerAudioProvider lavaPlayerAudioProviderMock;
  @Mock
  private VoiceChannel voiceChannelMock;
  @Mock
  private VoiceConnection voiceConnectionMock;

  @Test
  public void it_joins_a_voice_channel() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(messageCreateEventListenerMock.getMsgAuthor()).thenReturn(memberMock);
    Mockito.when(memberMock.getVoiceState()).thenReturn(Mono.just(voiceStateMock));
    Mockito.when(voiceStateMock.getChannel()).thenReturn(Mono.just(voiceChannelMock));
    Mockito.when(voiceChannelMock.join(Mockito.any(Consumer.class))).thenReturn(Mono.just(voiceConnectionMock));

    SummonTask summonTask = new SummonTask(lavaPlayerAudioProviderMock, messageCreateEventListenerMock, new LeaveTask());
    summonTask.action();

    Assertions.assertTrue(summonTask.getVoiceConnection().isPresent());
    Assertions.assertEquals(voiceConnectionMock, summonTask.getVoiceConnection().get());
  }
}
