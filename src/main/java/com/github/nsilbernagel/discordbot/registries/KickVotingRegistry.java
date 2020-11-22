package com.github.nsilbernagel.discordbot.registries;

import java.util.ArrayList;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.model.KickVoting;

import discord4j.core.object.entity.User;
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

  public Optional<KickVoting> getByUser(User user) {
    return this.votings.stream().filter(voting -> voting.getUserToKick().equals(user)).findFirst();
  }

  public Optional<KickVoting> createKickVoting(User user) {
    if (this.getByUser(user).isPresent()) {
      return Optional.empty();
    }
    KickVoting voting = new KickVoting(user, 3);
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
