package com.github.nsilbernagel.discordbot.vote;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.github.nsilbernagel.discordbot.vote.dto.Vote;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.http.client.ClientException;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

public abstract class Voting {
  @Getter
  private Message remainingVotesMessage;

  /** the member the voting is targeting */
  @Getter
  protected Member targetMember;

  @Getter
  @Setter
  private long votesNeeded;

  @Getter
  private final List<Vote> votes = new ArrayList<>();

  @Getter
  @Setter
  private int votesPerUser = 1;

  /** time until voting gets deleted */
  @Getter
  private Duration ttl = Duration.ofMinutes(10);

  /** the message because of which the voting was started */
  @Getter
  private final Message trigger;

  @Setter
  private Consumer<Voting> enoughVotesCallBack;

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
   */
  public void addVote(Vote vote) {
    this.votes.add(vote);
    this.updateRemainingVotesMessage().block();
    if (this.votes.size() >= this.votesNeeded) {
      this.onEnoughVotes();

      if(this.enoughVotesCallBack != null){
        this.enoughVotesCallBack.accept(this);
      }
    }
  }

  public void addVote(Member memberWhoVoted, Instant timestamp) {
    Vote voteByMember = new Vote(memberWhoVoted, timestamp);

    this.addVote(voteByMember);
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

  protected void sendRemainingVotesMessage() {
    this.remainingVotesMessage = this.getTrigger()
        .getChannel()
        .flatMap((channel) -> channel.createMessage(this.generateRemainingVotesMessage()))
        .block();
  }

  public Mono<Message> updateRemainingVotesMessage() {
    return this.remainingVotesMessage
        .edit(messageEditSpec -> messageEditSpec.setContent(this.generateRemainingVotesMessage()))
        // the message was probably deleted, just ignore that
        .onErrorResume(ClientException.class, (unused) -> Mono.empty());
  }

  /**
   * Generate a message that has info about how many users need to vote to kick the user
   */
  private String generateRemainingVotesMessage() {
    return "```\n" +
        "Noch " + this.remainingVotes() + " Stimmen bis " +
        this.getTargetMember().getDisplayName() + " gekickt wird." +
        "```";
  }

  /**
   * the ttl of the voting ran out
   */
  public void votingEnded() {
    this.getRemainingVotesMessage()
        .edit(messageEditSpec -> messageEditSpec.setContent("```\n" +
            "Abstimmung zum kicken von " + this.getTargetMember().getDisplayName() +" abgelaufen." +
            "```"))
        // the message was probably deleted, just ignore that
        .onErrorResume(ClientException.class, (unused) -> Mono.empty())
        .subscribe();
  }
}
