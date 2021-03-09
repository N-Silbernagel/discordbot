package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.message.TaskException;

import com.github.nsilbernagel.discordbot.vote.dto.Vote;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;

import java.time.Instant;

public class KickVoting extends Voting {
  final public static int requiredVoiceChannelMembers = 3;

  public KickVoting(Member memberToKick, Message trigger) {
    super(calculateVotesRequired(memberToKick), trigger);

    this.targetMember = memberToKick;
  }

  /**
   * Calculate the number of votes needed to kick someone from a voice channel
   * more than half of the member in the voice channel should vote and a
   * reasonable number of people should be inside of it
   */
  private static int calculateVotesRequired(Member memberToKick) {
    int membersInVoiceChannel = memberToKick.getVoiceState()
        .blockOptional()
        .orElseThrow(() -> new TaskException(memberToKick.getUsername() + " ist nicht in einem voice Channel"))
        .getChannel()
        .block()
        .getVoiceStates()
        .collectList()
        .block()
        .size();

    if (membersInVoiceChannel < requiredVoiceChannelMembers) {
      throw new TaskException("Im Voice Channel von " + memberToKick.getUsername() + " müssen mehr als "
          + requiredVoiceChannelMembers + " Nutzer sein.");
    }

    return (int) Math.floor(membersInVoiceChannel / 2) + 1;
  }

  @Override
  protected void onEnoughVotes() {
    this.targetMember.edit(memberSpec -> memberSpec.setNewVoiceChannel(null)).block();
  }

}
