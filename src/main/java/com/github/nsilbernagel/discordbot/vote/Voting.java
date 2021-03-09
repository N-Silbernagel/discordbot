package com.github.nsilbernagel.discordbot.vote;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.nsilbernagel.discordbot.vote.dto.Vote;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

public abstract class Voting {
  @Getter
  private Message remainingVotesMessage;

  @Getter
  /** the member the voting is targeting */
  protected Member targetMember;

  @Getter
  @Setter
  private long votesNeeded;

  @Getter
  private List<Vote> votes = new ArrayList<Vote>();

  @Getter
  @Setter
  private int votesPerUser = 1;

  @Getter
  /** time until voting gets deleted */
  private Duration ttl = Duration.ofMinutes(10);

  @Getter
  /** the message because of which the voting was started */
  private Message trigger;

  public Voting(long votesNeeded, Message trigger) {
    this.votesNeeded = votesNeeded;
    this.trigger = trigger;
  }

  public Voting(long votesNeeded, Message trigger, Duration ttl) {
    this.votesNeeded = votesNeeded;
    this.trigger = trigger;
    this.ttl = ttl;
  }

  /**
   * add a vote to the voting
   *
   * @return if votes were sufficient
   */
  public boolean addVote(Vote vote) {
    this.votes.add(vote);
    this.renewMessageWithNumberOfRemainingVotes().block();
    if (this.votes.size() < this.votesNeeded) {
      return false;
    }
    this.onEnoughVotes();
    return true;
  }

  public boolean addVote(Member memberWhoVoted, Instant timestamp) {
    Vote voteByMember = new Vote(memberWhoVoted, timestamp);

    return this.addVote(voteByMember);
  }

  public void removeVote(Vote vote) {
    this.votes.remove(vote);
  }

  public Vote lastVote() {
    return this.votes.get(this.votes.size() - 1);
  }

  public long remainingVotes() {
    return this.votesNeeded - this.votes.size() - 1;
  }

  public boolean memberHasVotedAsOftenAsHeMay(Member member) {
    List<Vote> votesByMember = this.votes
        .stream()
        .filter((vote) -> vote.getVoter().getId().equals(member.getId()))
        .collect(Collectors.toList());

    return votesByMember.size() >= this.votesPerUser;
  }

  /**
   * Action to be executed when the voting was successful
   */
  abstract protected void onEnoughVotes();

  protected void createMessageWithNumberOfRemainingVotes() {
    this.remainingVotesMessage = this.getTrigger()
            .getChannel()
            .flatMap((channel) -> channel.createMessage(this.generateRemainingVotesMessage()))
            .block();
  }

  public Mono<Message> renewMessageWithNumberOfRemainingVotes() {
    return this.remainingVotesMessage.edit(messageEditSpec -> messageEditSpec.setContent(this.generateRemainingVotesMessage()));
  }

  /**
   * Generate a message that has info about how many users need to vote to kick the user
   */
  private String generateRemainingVotesMessage() {
    return new StringBuilder("```\n")
            .append("Noch ").append(this.remainingVotes()).append(" Stimmen bis ")
            .append(this.getTargetMember().getDisplayName()).append(" gekickt wird.")
            .append("```")
            .toString();
  }
}
