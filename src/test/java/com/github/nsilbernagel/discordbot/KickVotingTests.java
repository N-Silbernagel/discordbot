package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.vote.KickVoting;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class KickVotingTests {
  @Mock
  private Member memberMock;
  @Mock
  private Message messageMock;
  @Mock
  private Message remainingVotesMessageMock;
  @Mock
  private MessageChannel channelMock;
  @Mock
  private VoiceState voiceStateMock;
  @Mock
  private VoiceChannel voiceChannelMock;

  private List<VoiceState> mockOtherVoiceStates(int count) {
    List<VoiceState> otherVoiceStates = new ArrayList<>(count);
    for (int i = 0; i < count; i++){
      otherVoiceStates.add(Mockito.mock(VoiceState.class));
    }

    return otherVoiceStates;
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);

    // mock remainingVotesMessage
    Mockito.when(messageMock.getChannel()).thenReturn(Mono.just(channelMock));
    Mockito.when(channelMock.createMessage(Mockito.any(String.class))).thenReturn(Mono.just(remainingVotesMessageMock));
  }

  @Test
  public void the_user_needs_to_be_in_a_voice_channel() {
    //mock that user is not in voice channel, thus having no voice state
    Mockito.when(memberMock.getVoiceState()).thenReturn(Mono.empty());

    Assertions.assertThrows(TaskException.class, () -> {
      new KickVoting(memberMock, messageMock);
    });
  }

  @Test
  public void there_is_threshold_for_how_many_users_need_to_be_in_the_voice_channel_to_kick_someone() {
    Mockito.when(memberMock.getVoiceState()).thenReturn(Mono.just(voiceStateMock));
    Mockito.when(voiceStateMock.getChannel()).thenReturn(Mono.just(voiceChannelMock));

    // test that it throws if there are less than the required members in the voice channel
    List<VoiceState> otherVoiceStates = this.mockOtherVoiceStates(KickVoting.requiredVoiceChannelMembers - 1);
    Mockito.when(voiceChannelMock.getVoiceStates()).thenReturn(Flux.fromIterable(otherVoiceStates));

    Assertions.assertThrows(TaskException.class, () -> {
      new KickVoting(memberMock, messageMock);
    });

    // test that it does not throw when there are at least as many members as needed in the voice channel
    otherVoiceStates = this.mockOtherVoiceStates(KickVoting.requiredVoiceChannelMembers);
    Mockito.when(voiceChannelMock.getVoiceStates()).thenReturn(Flux.fromIterable(otherVoiceStates));

    Assertions.assertDoesNotThrow(() -> {
      new KickVoting(memberMock, messageMock);
    });
  }

  @Test
  public void more_than_half_the_members_in_the_channel_need_to_have_voted() {
    Mockito.when(memberMock.getVoiceState()).thenReturn(Mono.just(voiceStateMock));
    Mockito.when(voiceStateMock.getChannel()).thenReturn(Mono.just(voiceChannelMock));

    // test that it does not throw when there are at least as many members as needed in the voice channel
    List<VoiceState> otherVoiceStates = this.mockOtherVoiceStates(10);
    Mockito.when(voiceChannelMock.getVoiceStates()).thenReturn(Flux.fromIterable(otherVoiceStates));

    KickVoting kickVoting = new KickVoting(memberMock, messageMock);

    Assertions.assertEquals(6, kickVoting.getVotesNeeded());
  }
}
