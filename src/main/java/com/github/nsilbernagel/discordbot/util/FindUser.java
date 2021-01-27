package com.github.nsilbernagel.discordbot.util;

import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public class FindUser {
  public static Optional<Member> getUserFromName(final String userID, VoiceChannel channel) {
    Mono<List<Member>> matchingUsers = channel.getVoiceStates().flatMap(VoiceState::getMember)
        .filter(member -> member.getId().asString().equals(userID))
        .collectList();
    try {
      return Optional.of(matchingUsers.block().get(0));
    } catch (IndexOutOfBoundsException e) {
      return Optional.empty();
    }
  }
}
