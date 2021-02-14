package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.vote.VotingRegistry;
import com.github.nsilbernagel.discordbot.vote.KickVoting;
import com.github.nsilbernagel.discordbot.vote.dto.Vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.rest.http.client.ClientException;

@Component
public class VoteKickTask extends AbstractMessageTask implements ExplainedMessageTask {
  public final static String KEYWORD = "votekick";

  @Autowired
  private VotingRegistry registry;

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  @Override
  public void action() {
    Guild guild = this.getMessage()
        .getGuild()
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
        .orElse(this.registry.createKickVoting(memberToKick, this.getMessage()));

    if (runningKickVoting.memberHasVotedAsOftenAsHeMay(this.messageCreateEventListener.getMsgAuthor())) {
      throw new TaskException("Du darfst nicht noch einmal an dieser Abstimmung teilnehmen.");
    }

    Vote voteByMsgAuthor = new Vote(this.messageCreateEventListener.getMsgAuthor(), this.getMessage().getTimestamp());

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

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Demokratie walten lassen.";
  }
}