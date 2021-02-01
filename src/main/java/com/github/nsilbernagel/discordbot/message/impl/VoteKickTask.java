package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.model.KickVoting;
import com.github.nsilbernagel.discordbot.model.Vote;
import com.github.nsilbernagel.discordbot.registries.KickVotingRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.http.client.ClientException;

@Component
public class VoteKickTask extends AbstractMessageTask implements IMessageTask {
  public final static String KEYWORD = "votekick";

  @Autowired
  private KickVotingRegistry registry;

  @Override
  public void execute(Message message) {
    this.message = message;

    Guild guild = this.message
        .getGuild()
        .doOnError(error -> {
          throw new TaskException();
        })
        .block();

    Member msgAuthor = this.message
        .getAuthorAsMember()
        .doOnError(error -> {
          throw new TaskException();
        })
        .block();

    Member memberToKick = this.message
        .getUserMentions()
        .doOnError(error -> {
          throw new TaskException("Bitte gib einen Nutzer an, indem du ihn mit '@NUTZER' markierst.");
        })
        .blockFirst()
        .asMember(guild.getId())
        .doOnError(error -> {
          throw new TaskException("Bitte gib einen Nutzer dieses Server an, indem du ihn mit '@NUTZER' markierst.");
        })
        .block();

    KickVoting runningKickVoting = this.registry
        .getByMember(memberToKick)
        .orElse(this.registry.createKickVoting(memberToKick));

    if (runningKickVoting.memberHasVoted(msgAuthor)) {
      throw new TaskException("Du hast bereits an dieser Abstimmung teilgenommen.");
    }

    Vote voteByMsgAuthor = new Vote(msgAuthor, this.message.getTimestamp());

    boolean enoughVotes = false;

    try {
      enoughVotes = runningKickVoting.addVote(voteByMsgAuthor);
    } catch (ClientException error) {
      throw new TaskException(memberToKick.getNickname().get() + " kann nicht gekickt werden.");
    }

    if (!enoughVotes) {
      this.answerMessage("Noch " + runningKickVoting.remainingVotes() + " Stimmen bis "
          + memberToKick.getDisplayName() + " rausgeworfen wird.");
    } else {
      this.registry.getVotings().remove(runningKickVoting);
      this.answerMessage(memberToKick.getDisplayName() + " gekickt.");
    }
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}