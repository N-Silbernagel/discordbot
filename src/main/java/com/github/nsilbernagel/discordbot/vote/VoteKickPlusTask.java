package com.github.nsilbernagel.discordbot.vote;

import java.time.Instant;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.reaction.ReactionAddEventListener;
import com.github.nsilbernagel.discordbot.reaction.ReactionTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.reaction.ReactionEmoji;

@Component
public class VoteKickPlusTask extends ReactionTask {
  final public ReactionEmoji TRIGGER = Emoji.CHECK.getUnicodeEmoji();

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

    kickVotingTriggeredByMessage.get().addVote(
        this.reactionAddEventListener.getReactionAddEvent()
            .getMember()
            .get(),
        Instant.now()
    );
  }
}
