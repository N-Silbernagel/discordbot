package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.task.TaskException;
import discord4j.common.util.Snowflake;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.GuildMemberEditSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
      VoiceState voiceStateMock = mock(VoiceState.class);
      Member memberMock = mock(Member.class);
      when(voiceStateMock.getMember()).thenReturn(Mono.just(memberMock));
      otherVoiceStates.add(voiceStateMock);
    }

    return otherVoiceStates;
  }

  private void addVote(KickVoting voting) {
    Member votedMemberMock = mock(Member.class);
    voting.addVote(votedMemberMock, Instant.now());
  }

  @Test
  public void the_user_needs_to_be_in_a_voice_channel() {
    //mock that user is not in voice channel, thus having no voice state
    when(memberMock.getVoiceState()).thenReturn(Mono.empty());

    Assertions.assertThrows(TaskException.class, () -> new KickVoting(memberMock, messageMock));
  }

  @Test
  public void there_is_a_threshold_for_how_many_users_need_to_be_in_the_voice_channel_to_kick_someone() {
    when(memberMock.getVoiceState()).thenReturn(Mono.just(voiceStateMock));
    when(voiceStateMock.getChannel()).thenReturn(Mono.just(voiceChannelMock));

    // test that it throws if there are less than the required members in the voice channel
    List<VoiceState> otherVoiceStates = this.mockOtherVoiceStates(KickVoting.requiredVoiceChannelMembers - 1);
    when(voiceChannelMock.getVoiceStates()).thenReturn(Flux.fromIterable(otherVoiceStates));

    Assertions.assertThrows(TaskException.class, () -> new KickVoting(memberMock, messageMock));

    // test that it does not throw when there are at least as many members as needed in the voice channel
    otherVoiceStates = this.mockOtherVoiceStates(KickVoting.requiredVoiceChannelMembers);
    when(voiceChannelMock.getVoiceStates()).thenReturn(Flux.fromIterable(otherVoiceStates));

    Assertions.assertDoesNotThrow(() -> {
      new KickVoting(memberMock, messageMock);
    });
  }

  @Test
  public void more_than_half_the_members_in_the_channel_need_to_vote() {
    when(memberMock.getVoiceState()).thenReturn(Mono.just(voiceStateMock));
    when(voiceStateMock.getChannel()).thenReturn(Mono.just(voiceChannelMock));

    // test that it does not throw when there are at least as many members as needed in the voice channel
    List<VoiceState> otherVoiceStates = this.mockOtherVoiceStates(10);
    when(voiceChannelMock.getVoiceStates()).thenReturn(Flux.fromIterable(otherVoiceStates));

    KickVoting kickVoting = new KickVoting(memberMock, messageMock);

    Assertions.assertEquals(6, kickVoting.getVotesNeeded());
  }

  @Test
  public void the_member_is_kicked_when_the_voting_ends() {
    when(memberMock.getVoiceState()).thenReturn(Mono.just(voiceStateMock));
    when(voiceStateMock.getChannel()).thenReturn(Mono.just(voiceChannelMock));

    // prepare "members" in voice channel
    List<VoiceState> otherVoiceStates = this.mockOtherVoiceStates(KickVoting.requiredVoiceChannelMembers);
    when(voiceChannelMock.getVoiceStates()).thenReturn(Flux.fromIterable(otherVoiceStates));

    KickVoting kickVoting = new KickVoting(memberMock, messageMock);

    when(memberMock.edit(any(Consumer.class))).thenReturn(Mono.empty());

    // capture the member edit consumer to verify that we are setting the voice channel of the spec to null, thus making the member leave the voice channel
    ArgumentCaptor<Consumer<GuildMemberEditSpec>> memberSpecConsumerCaptor = ArgumentCaptor.forClass(Consumer.class);

    for (int i = 0; i < kickVoting.getVotesNeeded(); i++) {
      this.addVote(kickVoting);
    }

    verify(memberMock).edit(memberSpecConsumerCaptor.capture());

    GuildMemberEditSpec guildMemberEditSpecMock = mock(GuildMemberEditSpec.class);

    when(guildMemberEditSpecMock.setNewVoiceChannel(null)).thenReturn(guildMemberEditSpecMock);

    // execute the callback on the member edit spec mock
    memberSpecConsumerCaptor.getValue().accept(guildMemberEditSpecMock);

    //verify that the voice channel of the members is being set and that is set to null, thus forcing the member to leave the voice channel
    ArgumentCaptor<Snowflake> nullVoiceChannelCaptor = ArgumentCaptor.forClass(Snowflake.class);
    verify(guildMemberEditSpecMock).setNewVoiceChannel(nullVoiceChannelCaptor.capture());

    Assertions.assertNull(nullVoiceChannelCaptor.getValue());
  }

  @Test
  public void bots_are_not_considered_when_calculating_required_votes(){
    when(memberMock.getVoiceState()).thenReturn(Mono.just(voiceStateMock));
    when(voiceStateMock.getChannel()).thenReturn(Mono.just(voiceChannelMock));

    // test that it does throw when there are at enough members but one is a bot
    List<VoiceState> otherVoiceStates = this.mockOtherVoiceStates(KickVoting.requiredVoiceChannelMembers);

    Member botMemberMock = mock(Member.class);
    Mono<Member> memberMonoMock = mock(Mono.class);
    when(memberMonoMock.block()).thenReturn(botMemberMock);
    when(botMemberMock.isBot()).thenReturn(true);

    when(otherVoiceStates.get(1).getMember()).thenReturn(memberMonoMock);
    when(voiceChannelMock.getVoiceStates()).thenReturn(Flux.fromIterable(otherVoiceStates));

    Assertions.assertThrows(TaskException.class, () -> {
      new KickVoting(memberMock, messageMock);
    });
  }
}
