package com.github.nsilbernagel.discordbot.vote;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Component
public class VotingRegistry {

  @Getter
  @Setter
  private ArrayList<AbstractVoting> votings;

  public VotingRegistry() {
    this.votings = new ArrayList<AbstractVoting>();
  }

  public <T extends AbstractVoting> Optional<T> getByMember(Member member, Class<T> votingClass) {
    return this.votings.stream()
        .filter(voting -> voting.getClass().equals(votingClass) && voting.getTargetMember().equals(member))
        .map(voting -> votingClass.cast(voting))
        .findFirst();
  }

  public KickVoting createKickVoting(Member member) {
    KickVoting voting = new KickVoting(member);
    this.votings.add(voting);
    return voting;
  }
}
