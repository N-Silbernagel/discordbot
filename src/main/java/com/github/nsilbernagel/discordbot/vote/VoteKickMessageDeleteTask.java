package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.message.MessageDeleteTask;
import discord4j.common.util.Snowflake;
import discord4j.rest.http.client.ClientException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class VoteKickMessageDeleteTask extends MessageDeleteTask {
  private final VotingRegistry votingRegistry;


  public VoteKickMessageDeleteTask(VotingRegistry votingRegistry) {
    this.votingRegistry = votingRegistry;
  }

  @Override
  public void execute(Snowflake channelId, Snowflake messageId) {
    this.votingRegistry.getByTriggerId(messageId, KickVoting.class)
        .ifPresent(this::deleteVoting);
  }

  private void deleteVoting(KickVoting voting) {
    voting.getRemainingVotesMessage()
        .edit(messageEditSpec -> messageEditSpec.setContent("```" +
            "Abstimmung zum kicken von " + voting.getTargetMember().getDisplayName() + " abgebrochen" +
            "```"))
        .onErrorResume(ClientException.class, (un) -> Mono.empty())
        .block();
    this.votingRegistry.getVotings()
        .remove(voting);
  }
}
