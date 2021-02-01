package com.github.nsilbernagel.discordbot.registries;

import java.util.ArrayList;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.model.KickVoting;

import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Component
public class KickVotingRegistry {

  @Getter
  @Setter
  private ArrayList<KickVoting> votings;

  public KickVotingRegistry() {
    this.votings = new ArrayList<KickVoting>();
  }

  public Optional<KickVoting> getByMember(Member member) {
    return this.votings.stream().filter(voting -> voting.getMemberToKick().equals(member)).findFirst();
  }

  public KickVoting createKickVoting(Member member) {
    KickVoting voting = new KickVoting(member, 3);
    this.votings.add(voting);
    return voting;
  }
}
