package com.github.nsilbernagel.discordbot.vote.dto;

import java.time.Instant;

import discord4j.core.object.entity.Member;
import lombok.Getter;

public class Vote {
  @Getter
  private final Member voter;
  @Getter
  private final Instant time;

  public Vote(Member member, Instant time) {
    this.voter = member;
    this.time = time;
  }
}
