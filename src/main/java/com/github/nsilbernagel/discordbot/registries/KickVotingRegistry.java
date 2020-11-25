package com.github.nsilbernagel.discordbot.registries;

import java.util.ArrayList;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.model.KickVoting;

import discord4j.core.object.entity.Member;
import lombok.Getter;
import lombok.Setter;

public class KickVotingRegistry {
  private static KickVotingRegistry instance;

  @Getter
  @Setter
  private ArrayList<KickVoting> votings;

  private KickVotingRegistry() {
    this.votings = new ArrayList<KickVoting>();
  }

  public Optional<KickVoting> getByMember(Member member) {
    return this.votings.stream().filter(voting -> voting.getMemberToKick().equals(member)).findFirst();
  }

  public Optional<KickVoting> createKickVoting(Member member) {
    if (this.getByMember(member).isPresent()) {
      return Optional.empty();
    }
    KickVoting voting = new KickVoting(member, 3);
    this.votings.add(voting);
    return Optional.of(voting);
  }

  public static KickVotingRegistry getInstance() {
    if (KickVotingRegistry.instance == null) {
      KickVotingRegistry.instance = new KickVotingRegistry();
    }
    return KickVotingRegistry.instance;
  }
}
