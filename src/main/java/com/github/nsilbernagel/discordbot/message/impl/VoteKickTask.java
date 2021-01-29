package com.github.nsilbernagel.discordbot.message.impl;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskLogicException;
import com.github.nsilbernagel.discordbot.model.KickVoting;
import com.github.nsilbernagel.discordbot.model.Vote;
import com.github.nsilbernagel.discordbot.registries.KickVotingRegistry;

import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

@Component
public class VoteKickTask extends AbstractMessageTask implements IMessageTask {
  private final static String KEYWORD = "votekick";

  private KickVotingRegistry registry = KickVotingRegistry.getInstance();

  @Override
  public void execute(Message message) {
    this.message = message;
    Snowflake guildId = this.message.getGuildId().orElseThrow(() -> new TaskLogicException());

    User msgAuthor = this.message.getAuthor().orElseThrow(() -> new TaskLogicException());
    Member msgAuthorAsMember = userAsMemberOfGuild(msgAuthor, guildId);

    User userToKick = this.message.getUserMentions().blockFirst();
    if (userToKick == null || userToKick.isBot()) {
      throw new TaskLogicException("Bitte gebe einen Nutzer an, indem du ihn mit '@NUTZER' markierst.");
    }

    Member memberToKick = userAsMemberOfGuild(userToKick, guildId);

    Optional<KickVoting> runningKickVoting = this.registry.getByMember(memberToKick);
    if (!runningKickVoting.isPresent()) {
      runningKickVoting = this.registry.createKickVoting(memberToKick);
    }

    if (runningKickVoting.get().userHasVoted(msgAuthor)) {
      this.answerMessage("Du hast bereits an dieser Abstimmung teilgenommen.");
      return;
    }

    Vote voteByMsgAuthor = new Vote(msgAuthorAsMember, this.message.getTimestamp());

    boolean enoughVotes = runningKickVoting.get().addVote(voteByMsgAuthor);

    if (!enoughVotes) {
      this.answerMessage("Noch " + runningKickVoting.get().remainingVotes() + " Stimmen bis "
          + memberToKick.getDisplayName() + " rausgeworfen wird.");
    } else {
      this.registry.getVotings().remove(runningKickVoting.get());
      this.answerMessage(memberToKick.getDisplayName() + " gekickt.");
    }
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}