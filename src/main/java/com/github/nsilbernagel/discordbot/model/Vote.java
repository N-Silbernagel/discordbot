package com.github.nsilbernagel.discordbot.model;

import java.time.Instant;

import discord4j.core.object.entity.User;
import lombok.Getter;

public class Vote {
  @Getter
  private User voter;
  @Getter
  private Instant time;

  public Vote(User user, Instant time) {
    this.voter = user;
    this.time = time;
  }
}
