package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.interaction.InteractionTask;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import discord4j.common.util.Snowflake;
import discord4j.rest.util.Permission;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@NeedsPermission(Permission.ADMINISTRATOR)
public class TimeVaryingChannelUnsetInteractionTask extends InteractionTask {
  public final String NO_CHANNEL_MESSAGE = "Es ist kein channel konfiguriert.";
  public final String UNSET_MESSAGE = "Channel entfernt.";

  private final TimeVaryingChannelRepo timeVaryingChannelRepo;

  public TimeVaryingChannelUnsetInteractionTask(TimeVaryingChannelRepo timeVaryingChannelRepo) {
    this.timeVaryingChannelRepo = timeVaryingChannelRepo;
  }

  @Override
  public void action(InteractionTaskRequest request) {
    Optional<Snowflake> guildId = request.getEvent().getInteraction().getGuildId();
    Optional<TimeVaryingChannelEntity> timeVaryingChannelEntity = timeVaryingChannelRepo.findByguildId(guildId.get().asLong());

    if (timeVaryingChannelEntity.isEmpty()){
      request.getEvent().replyEphemeral(NO_CHANNEL_MESSAGE).block();
      return;
    }

    this.timeVaryingChannelRepo.delete(timeVaryingChannelEntity.get());
    request.getEvent().replyEphemeral(UNSET_MESSAGE).block();
  }

  @Override
  public boolean canHandle(String command) {
    return command.equals("timechannel/unset");
  }
}
