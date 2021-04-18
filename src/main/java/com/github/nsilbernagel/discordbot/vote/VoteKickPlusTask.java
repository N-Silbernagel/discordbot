package com.github.nsilbernagel.discordbot.vote;

import java.time.Instant;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.reaction.ReactionTask;

import com.github.nsilbernagel.discordbot.reaction.ReactionTaskRequest;
import discord4j.rest.http.client.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.reaction.ReactionEmoji;
import reactor.core.publisher.Mono;

@Component
public class VoteKickPlusTask extends ReactionTask {
  final public ReactionEmoji TRIGGER = Emoji.CHECK.getUnicodeEmoji();

  @Autowired
  private VotingRegistry votingRegistry;

  public boolean canHandle(ReactionEmoji reactionEmoji) {
    return reactionEmoji.equals(TRIGGER);
  }

  public ReactionEmoji getTrigger() {
    return TRIGGER;
  }

  public void action(ReactionTaskRequest taskRequest) {
    // get kickvoting by trigger message
    Optional<KickVoting> kickVotingTriggeredByMessage = this.votingRegistry
        .getByTrigger(taskRequest.getMessage(), KickVoting.class);

    if (kickVotingTriggeredByMessage.isEmpty()) {
      return;
    }

    if (kickVotingTriggeredByMessage.get().memberHasVotedAsOftenAsHeMay(taskRequest.getAuthor())) {
      return;
    }

    kickVotingTriggeredByMessage.get().addVote(
        taskRequest.getAuthor(),
        Instant.now()
    );

    kickVotingTriggeredByMessage.get().getRemainingVotesMessage()
        .edit(messageEditSpec -> messageEditSpec.setContent("```" +
            "Noch " + kickVotingTriggeredByMessage.get().remainingVotes() + " Stimmen bis " + kickVotingTriggeredByMessage.get().getTargetMember().getDisplayName() + " gekickt wird." +
            "```"))
        .onErrorResume(ClientException.class, (unused) -> Mono.empty())
        .block();
  }
}
