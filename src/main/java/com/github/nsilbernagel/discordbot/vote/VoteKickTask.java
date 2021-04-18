package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.BeanUtil;
import com.github.nsilbernagel.discordbot.message.*;
import com.github.nsilbernagel.discordbot.task.TaskException;

import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;

import java.util.Optional;

@Component
public class VoteKickTask extends MessageTask implements ExplainedMessageTask {
  public final static String KEYWORD = "votekick";

  private final VotingRegistry registry;
  private final VoteKickMessageDeleteTask voteKickMessageDeleteTask;


  private final VoteKickPlusTask voteKickPlusTask;

  public VoteKickTask(VotingRegistry registry, VoteKickPlusTask voteKickPlusTask, VoteKickMessageDeleteTask voteKickMessageDeleteTask) {
    this.registry = registry;
    this.voteKickPlusTask = voteKickPlusTask;
    this.voteKickMessageDeleteTask = voteKickMessageDeleteTask;
  }

  /**
   * Crate a Kickvoting
   * @see KickVoting
   */
  @Override
  public void action(MsgTaskRequest taskRequest) {
    Guild guild = taskRequest.getMessage()
        .getGuild()
        .block();

    assert guild != null;

    Optional<Member> memberToKick = Optional.ofNullable(
        taskRequest.getMessage()
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

    KickVoting newKickVoting = new KickVoting(memberToKick.get(), taskRequest.getMessage());

    this.voteKickMessageDeleteTask.addDeletableMessage(new MessageInChannel(
        taskRequest.getChannel().getId(),
        taskRequest.getMessage().getId()
    ));

    newKickVoting.setEnoughVotesCallBack((kickVoting) ->
        BeanUtil.getSpringContext().publishEvent(new VotingFinishedEvent(this, kickVoting))
    );
    this.registry.addVoting(newKickVoting);
    this.voteKickPlusTask.addMessage(taskRequest.getMessage());

    newKickVoting.addVote(
        taskRequest.getAuthor(),
        taskRequest.getMessage().getTimestamp()
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