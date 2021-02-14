package com.github.nsilbernagel.discordbot.reaction.impl;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.listeners.impl.ReactionAddEventListener;
import com.github.nsilbernagel.discordbot.reaction.AbstractReactionTask;
import com.github.nsilbernagel.discordbot.vote.KickVoting;
import com.github.nsilbernagel.discordbot.vote.VotingRegistry;
import com.github.nsilbernagel.discordbot.vote.dto.Vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.reaction.ReactionEmoji;

@Component
public class VoteKickPlusTask extends AbstractReactionTask {
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

    if (!kickVotingTriggeredByMessage.isPresent()) {
      return;
    }

    // add vote to kickvoting
    kickVotingTriggeredByMessage.get().addVote(new Vote(
        this.reactionAddEventListener.getMessage().getAuthorAsMember().block(),
        this.reactionAddEventListener.getMessage().getTimestamp()));
  }
}
