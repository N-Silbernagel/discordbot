package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.reaction.impl.VoteKickPlusTask;
import com.github.nsilbernagel.discordbot.vote.VotingRegistry;
import com.github.nsilbernagel.discordbot.vote.KickVoting;

import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;

import java.util.Optional;

@Component
public class VoteKickTask extends MessageTask implements ExplainedMessageTask {
  public final static String KEYWORD = "votekick";

  private final VotingRegistry registry;

  private final MessageCreateEventListener messageCreateEventListener;

  private final VoteKickPlusTask voteKickPlusTask;

  public VoteKickTask(VotingRegistry registry, MessageCreateEventListener messageCreateEventListener, VoteKickPlusTask voteKickPlusTask) {
    this.registry = registry;
    this.messageCreateEventListener = messageCreateEventListener;
    this.voteKickPlusTask = voteKickPlusTask;
  }

  /**
   * Crate a Kickvoting
   * @see KickVoting
   */
  @Override
  public void action() {
    Guild guild = this.getMessage()
        .getGuild()
        .block();

    assert guild != null;

    Optional<Member> memberToKick = Optional.ofNullable(
        this.getMessage()
        .getUserMentions()
        .filter((userMention) -> !userMention.isBot())
        .flatMap(user -> user.asMember(guild.getId()))
        .blockFirst()
    );

    if (memberToKick.isEmpty()) {
      throw new TaskException("Bitte gib einen Nutzer an, indem du ihn mit '@NUTZER' markierst.");
    }

    Optional<KickVoting> runningKickVoting = this.registry.getByMember(memberToKick.get(), KickVoting.class);

    if (runningKickVoting.isPresent()) {
      throw new TaskException("Es läuft bereits eine Abstimmung zum kicken von " + runningKickVoting.get().getTargetMember().getDisplayName());
    }

    KickVoting newKickVoting = new KickVoting(memberToKick.get(), this.getMessage());
    this.registry.addVoting(newKickVoting);
    this.voteKickPlusTask.addMessage(this.getMessage());

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