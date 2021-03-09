package com.github.nsilbernagel.discordbot.vote;

import java.util.ArrayList;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.reaction.impl.VoteKickPlusTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import lombok.Getter;
import lombok.Setter;

@Component
public class VotingRegistry {

  @Getter
  @Setter
  private ArrayList<Voting> votings;

  @Autowired
  private VoteKickPlusTask voteKickPlusTask;

  public VotingRegistry() {
    this.votings = new ArrayList<Voting>();
  }

  public <T extends Voting> Optional<T> getByMember(Member member, Class<T> votingClass) {
    return this.votings.stream()
        .filter(voting -> voting.getClass().equals(votingClass) && voting.getTargetMember().equals(member))
        .map(votingClass::cast)
        .findFirst();
  }

  public <T extends AbstractVoting> Optional<T> getByTrigger(Message trigger, Class<T> votingClass) {
    return this.votings.stream()
        .filter(voting -> voting.getClass().equals(votingClass) && voting.getTrigger().equals(trigger))
        .map(voting -> votingClass.cast(voting))
        .findFirst();
  }

  public KickVoting createKickVoting(Member member, Message trigger) {
    KickVoting voting = new KickVoting(member, trigger);
    this.votings.add(voting);
    this.voteKickPlusTask.addMessage(trigger);
    return voting;
  }
}
