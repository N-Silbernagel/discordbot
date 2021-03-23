package com.github.nsilbernagel.discordbot.vote;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class VotingFinishedEvent extends ApplicationEvent {
  @Getter
  private final Voting finishedVoting;

  public VotingFinishedEvent(Object source, Voting finishedVoting) {
    super(source);
    this.finishedVoting = finishedVoting;
  }
}
