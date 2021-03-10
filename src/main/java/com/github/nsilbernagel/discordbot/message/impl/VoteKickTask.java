package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.vote.VotingRegistry;
import com.github.nsilbernagel.discordbot.vote.KickVoting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;

import java.util.Optional;

@Component
public class VoteKickTask extends MessageTask implements ExplainedMessageTask {
  public final static String KEYWORD = "votekick";

  @Autowired
  private VotingRegistry registry;

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  /**
   * Crate a Kickvoting
   * @see KickVoting
   */
  @Override
  public void action() {
    Guild guild = this.getMessage()
        .getGuild()
        .doOnError(error -> {
          throw new TaskException(error);
        })
        .block();

    Member memberToKick;

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

    Optional<KickVoting> runningKickVoting = this.registry.getByMember(memberToKick, KickVoting.class);

    if (runningKickVoting.isPresent()) {
      throw new TaskException("Es läuft bereits eine Abstimmung zum kicken von " + runningKickVoting.get().getTargetMember().getDisplayName());
    }

    KickVoting newKickVoting = this.registry.createKickVoting(memberToKick, this.getMessage());
    newKickVoting.addVote(
        this.messageCreateEventListener.getMsgAuthor(),
        this.getMessage().getTimestamp()
    );
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