package com.github.nsilbernagel.discordbot.vote;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import discord4j.core.object.entity.Member;
import lombok.Getter;
import lombok.Setter;

abstract class AbstractVoting {
  @Getter
  @Setter
  private long votesNeeded;

  @Getter
  private List<Vote> votes;

  @Getter
  @Setter
  private int votesPerUser;

  @Getter
  /** time until voting gets deleted */
  private Duration ttl;

  public AbstractVoting(long votesNeeded, int votesPerUser, Duration ttl) {
    this.votesNeeded = votesNeeded;
    this.votesPerUser = votesPerUser;
    this.ttl = ttl;
    this.votes = new ArrayList<Vote>();
  }

  /**
   * add a vote to the voting
   *
   * @return wether action was executed or not
   */
  public boolean addVote(Vote vote) {
    this.votes.add(vote);
    if (this.votes.size() < this.votesNeeded) {
      return false;
    }
    this.onEnoughVotes();
    return true;
  }

  public void removeVote(Vote vote) {
    this.votes.remove(vote);
  }

  public Vote lastVote() {
    return this.votes.get(this.votes.size() - 1);
  }

  public long remainingVotes() {
    return this.votesNeeded - this.votes.size();
  }

  public boolean memberHasVoted(Member member) {
    return this.votes
        .stream()
        .filter((vote) -> vote.getVoter().getId().compareTo(member.getId()) == 0)
        .findFirst()
        .isPresent();
  }

  /**
   * Action to be executed when the voting was successfull
   *
   * @return success
   */
  abstract protected void onEnoughVotes();
}
