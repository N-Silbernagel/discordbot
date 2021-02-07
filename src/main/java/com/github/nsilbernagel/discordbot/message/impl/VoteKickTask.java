package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.registries.VotingRegistry;
import com.github.nsilbernagel.discordbot.vote.KickVoting;
import com.github.nsilbernagel.discordbot.vote.Vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.rest.http.client.ClientException;

@Component
public class VoteKickTask extends AbstractMessageTask implements IMessageTask {
  public final static String KEYWORD = "votekick";

  @Autowired
  private VotingRegistry registry;

  @Override
  public void execute() {
    Guild guild = this.getMessage()
        .getGuild()
        .doOnError(error -> {
          throw new TaskException(error);
        })
        .block();

    Member msgAuthor = this.getMessage()
        .getAuthorAsMember()
        .doOnError(error -> {
          throw new TaskException(error);
        })
        .block();

    Member memberToKick = null;

    try {
      memberToKick = this.getMessage()
          .getUserMentions()
          .filter((userMention) -> !userMention.isBot())
          .blockFirst()
          .asMember(guild.getId())
          .block();
    } catch (Throwable error) {
      throw new TaskException("Bitte gib einen Nutzer an, indem du ihn mit '@NUTZER' markierst.");
    }

    KickVoting runningKickVoting = this.registry
        .getByMember(memberToKick, KickVoting.class)
        .orElse(this.registry.createKickVoting(memberToKick));

    if (runningKickVoting.memberHasVoted(msgAuthor)) {
      throw new TaskException("Du hast bereits an dieser Abstimmung teilgenommen.");
    }

    Vote voteByMsgAuthor = new Vote(msgAuthor, this.getMessage().getTimestamp());

    boolean enoughVotes = false;

    try {
      enoughVotes = runningKickVoting.addVote(voteByMsgAuthor);
    } catch (ClientException error) {
      throw new TaskException(memberToKick.getNickname().get() + " kann nicht gekickt werden.");
    }

    if (!enoughVotes) {
      this.answerMessage("Noch " + runningKickVoting.remainingVotes() + " Stimmen bis "
          + memberToKick.getDisplayName() + " rausgeworfen wird.").block();
    } else {
      this.registry.getVotings().remove(runningKickVoting);
      this.answerMessage(memberToKick.getDisplayName() + " gekickt.").block();
    }
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}