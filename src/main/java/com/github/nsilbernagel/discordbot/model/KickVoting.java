package com.github.nsilbernagel.discordbot.model;

import java.time.Duration;

import discord4j.core.object.entity.User;
import lombok.Getter;

public class KickVoting extends AbstractVoting {
  @Getter
  private User userToKick;

  public KickVoting(User userToKick, long votesNeeded) {
    super(votesNeeded, 1, Duration.ofMinutes(10));

    this.userToKick = userToKick;
  }

  @Override
  protected boolean onEnoughVotes() {
    return true;
  }

}
