package com.github.nsilbernagel.discordbot.vote.dto;

import java.time.Instant;

import discord4j.core.object.entity.Member;
import lombok.Data;

@Data
public class Vote {
  private final Member voter;
  private final Instant time;
}
