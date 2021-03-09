package com.github.nsilbernagel.discordbot.reaction.impl;

import java.time.Instant;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.listeners.impl.ReactionAddEventListener;
import com.github.nsilbernagel.discordbot.reaction.ReactionTask;
import com.github.nsilbernagel.discordbot.vote.KickVoting;
import com.github.nsilbernagel.discordbot.vote.VotingRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.reaction.ReactionEmoji;

@Component
public class VoteKickPlusTask extends ReactionTask {
  final public ReactionEmoji TRIGGER = ReactionEmoji.unicode("✅");

  @Autowired
  private VotingRegistry votingRegistry;

  @Autowired
  private ReactionAddEventListener reactionAddEventListener;

  public boolean canHandle(ReactionEmoji reactionEmoji) {
    return reactionEmoji.equals(TRIGGER);
  }

  public ReactionEmoji getTrigger() {
    return TRIGGER;
  }

  public void action() {
    // get kickvoting by trigger message
    Optional<KickVoting> kickVotingTriggeredByMessage = this.votingRegistry
        .getByTrigger(this.reactionAddEventListener.getMessage(), KickVoting.class);

    if (kickVotingTriggeredByMessage.isEmpty()) {
      return;
    }

    if (kickVotingTriggeredByMessage.get().memberHasVotedAsOftenAsHeMay(this.reactionAddEventListener.getReactionAddEvent().getMember().get())) {
      return;
    }

    boolean enoughVotes = kickVotingTriggeredByMessage.get().addVote(
            this.reactionAddEventListener.getReactionAddEvent().getMember().get(),
            Instant.now()
    );

    if (!enoughVotes) {
      kickVotingTriggeredByMessage.get().renewMessageWithNumberOfRemainingVotes()
        .block();
    } else {
      kickVotingTriggeredByMessage.get().getRemainingVotesMessage()
        .delete()
        .block();
    }
  }
}
