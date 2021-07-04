package com.github.nsilbernagel.discordbot.vote.dto;

import java.time.Instant;

import discord4j.core.object.entity.Member;
import lombok.Data;

public record Vote(Member voter, Instant time) {
}
