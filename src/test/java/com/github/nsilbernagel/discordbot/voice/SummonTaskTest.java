package com.github.nsilbernagel.discordbot.voice;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.voice.LeaveTask;
import com.github.nsilbernagel.discordbot.voice.SummonTask;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.VoiceConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
public class SummonTaskTest {
  @Mock
  private Member memberMock;
  @Mock
  private VoiceState voiceStateMock;
  @Mock
  private LavaPlayerAudioProvider lavaPlayerAudioProviderMock;
  @Mock
  private VoiceChannel voiceChannelMock;
  @Mock
  private VoiceConnection voiceConnectionMock;
  @Mock
  private MsgTaskRequest taskRequestMock;

  @Test
  public void it_joins_a_voice_channel() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(memberMock.getVoiceState()).thenReturn(Mono.just(voiceStateMock));
    Mockito.when(voiceStateMock.getChannel()).thenReturn(Mono.just(voiceChannelMock));
    Mockito.when(voiceChannelMock.join(Mockito.any(Consumer.class))).thenReturn(Mono.just(voiceConnectionMock));
    Mockito.when(taskRequestMock.getAuthor()).thenReturn(this.memberMock);

    SummonTask summonTask = new SummonTask(lavaPlayerAudioProviderMock, new LeaveTask());
    summonTask.execute(taskRequestMock);

    Assertions.assertTrue(summonTask.getVoiceConnection().isPresent());
    Assertions.assertEquals(voiceConnectionMock, summonTask.getVoiceConnection().get());
  }
}
