package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.message.*;
import com.github.nsilbernagel.discordbot.task.TaskException;

import discord4j.core.object.entity.Message;
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

    newKickVoting.setEnoughVotesCallBack(this::handleFinishedVoting);

    this.registry.addVoting(newKickVoting);
    this.voteKickPlusTask.addMessage(taskRequest.getMessage());

    newKickVoting.addVote(
        taskRequest.getAuthor(),
        taskRequest.getMessage().getTimestamp()
    );

    Message remainingVotesMessage = newKickVoting.getTrigger()
        .getChannel()
        .flatMap((channel) -> channel.createMessage("```" +
            "Noch " + newKickVoting.remainingVotes() + " Stimmen bis " + newKickVoting.getTargetMember().getDisplayName() + " gekickt wird." +
            "```"))
        .block();

    newKickVoting.setRemainingVotesMessage(remainingVotesMessage);
  }

  private void handleFinishedVoting(Voting kickVoting) {
    kickVoting.getRemainingVotesMessage()
        .edit(messageEditSpec -> messageEditSpec.setContent("```" + kickVoting.targetMember.getDisplayName() +" wurde gekickt.```"))
        .block();
    this.registry.getVotings().remove(kickVoting);
    this.voteKickPlusTask.removeMessage(kickVoting.getTrigger());
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