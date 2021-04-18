package com.github.nsilbernagel.discordbot.vote;

import java.util.ArrayList;
import java.util.Optional;

import discord4j.common.util.Snowflake;
import discord4j.rest.http.client.ClientException;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import lombok.Getter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class VotingRegistry {

  @Getter
  private final ArrayList<Voting> votings;

  public VotingRegistry() {
    this.votings = new ArrayList<>();
  }

  public void addVoting(Voting newVoting) {
    this.votings.add(newVoting);
    Mono.just(newVoting)
        .delayElement(newVoting.getTtl(), Schedulers.single())
        .doOnSuccess(this::cleanUpVoting)
        .subscribe();
  }

  public void cleanUpVoting(Voting voting) {
    voting.getRemainingVotesMessage()
        .edit(messageEditSpec -> messageEditSpec.setContent("```\n" +
            "Abstimmung zum kicken von " + voting.getTargetMember().getDisplayName() +" abgelaufen." +
            "```"))
        // the message was probably deleted, just ignore that
        .onErrorResume(ClientException.class, (unused) -> Mono.empty())
        .subscribe();
    this.votings.remove(voting);
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

  public <T extends Voting> Optional<T> getByTriggerId(Snowflake triggerId, Class<T> votingClass) {
    return this.votings.stream()
        .filter(voting -> voting.getClass().equals(votingClass) && voting.getTrigger().getId().equals(triggerId))
        .map(votingClass::cast)
        .findFirst();
  }
}
