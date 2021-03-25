package com.github.nsilbernagel.discordbot.vote;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import lombok.Getter;

@Component
public class VotingRegistry {


  @Getter
  private final ArrayList<Voting> votings;

  @Autowired
  private VoteKickPlusTask voteKickPlusTask;

  public VotingRegistry() {
    this.votings = new ArrayList<>();
  }

  public void addVoting(Voting newVoting) {
    this.votings.add(newVoting);
  }

  @EventListener
  public void handleVotingFinishedEvent(VotingFinishedEvent votingFinishedEvent) {
    votingFinishedEvent.getFinishedVoting()
        .getRemainingVotesMessage()
        .delete()
        .block();
    this.votings.remove(votingFinishedEvent.getFinishedVoting());
    this.voteKickPlusTask.removeMessage(votingFinishedEvent.getFinishedVoting().getTrigger());
  }

  public <T extends Voting> Optional<T> getByMember(Member member, Class<T> votingClass) {
    return this.votings.stream()
        .filter(voting -> voting.getClass().equals(votingClass) && voting.getTargetMember().equals(member))
        .map(votingClass::cast)
        .findFirst();
  }

  public <T extends Voting> Optional<T> getByTrigger(Message trigger, Class<T> votingClass) {
    return this.votings.stream()
        .filter(voting -> voting.getClass().equals(votingClass) && voting.getTrigger().equals(trigger))
        .map(votingClass::cast)
        .findFirst();
  }
}
