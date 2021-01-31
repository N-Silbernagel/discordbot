package com.github.nsilbernagel.discordbot.model;

import java.time.Duration;

import discord4j.core.object.entity.Member;
import discord4j.rest.http.client.ClientException;
import lombok.Getter;

public class KickVoting extends AbstractVoting {
  @Getter
  private Member memberToKick;

  public KickVoting(Member memberToKick, long votesNeeded) {
    super(votesNeeded, 1, Duration.ofMinutes(10));

    this.memberToKick = memberToKick;
  }

  @Override
  protected void onEnoughVotes() throws ClientException {
    this.memberToKick.kick("Demokratie.").block();
  }

}
