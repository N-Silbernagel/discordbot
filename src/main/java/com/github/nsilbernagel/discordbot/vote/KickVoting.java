package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.message.TaskException;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.VoiceChannel;
import reactor.core.publisher.Flux;

import java.util.function.Function;


public class KickVoting extends Voting {
  final public static int requiredVoiceChannelMembers = 3;

  public KickVoting(Member memberToKick, Message trigger) {
    super(calculateVotesRequired(memberToKick), trigger);

    this.targetMember = memberToKick;
    this.createMessageWithNumberOfRemainingVotes();
  }

  /**
   * Calculate the number of votes needed to kick someone from a voice channel
   * more than half of the member in the voice channel should vote and a
   * reasonable number of people should be inside of it
   */
  private static long calculateVotesRequired(Member memberToKick) throws AssertionError {
    Long membersInVoiceChannel = memberToKick.getVoiceState()
        .blockOptional()
        .orElseThrow(() -> new TaskException(memberToKick.getUsername() + " ist nicht in einem voice Channel"))
        .getChannel()
        .map(VoiceChannel::getVoiceStates)
        .flatMap(Flux::count)
        .block();

    assert membersInVoiceChannel != null;

    if (membersInVoiceChannel < requiredVoiceChannelMembers) {
      throw new TaskException("Im Voice Channel von " + memberToKick.getUsername() + " müssen mehr als "
          + requiredVoiceChannelMembers + " Nutzer sein.");
    }

    return (int) Math.floor((double) membersInVoiceChannel / 2) + 1;
  }

  @Override
  protected void onEnoughVotes() {
    this.targetMember.edit(memberSpec -> memberSpec.setNewVoiceChannel(null)).block();
  }

}
