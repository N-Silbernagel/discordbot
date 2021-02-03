package com.github.nsilbernagel.discordbot.vote;

import java.time.Duration;

import discord4j.core.object.entity.Member;
import discord4j.rest.http.client.ClientException;

public class KickVoting extends AbstractVoting {
  public KickVoting(Member memberToKick, long votesNeeded) {
    super(votesNeeded, 1, Duration.ofMinutes(10));

    this.targetMember = memberToKick;
  }

  @Override
  protected void onEnoughVotes() throws ClientException {
    this.targetMember.kick("Demokratie.").block();
  }

}
