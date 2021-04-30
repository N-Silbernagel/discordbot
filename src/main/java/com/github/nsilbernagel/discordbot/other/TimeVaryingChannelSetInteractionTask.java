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
public class TimeVaryingChannelSetInteractionTask extends InteractionTask {
  public final String PERSIST_MESSAGE = "Channel set.";

  private final TimeVaryingChannelRepo timeVaryingChannelRepo;

  public TimeVaryingChannelSetInteractionTask(TimeVaryingChannelRepo timeVaryingChannelRepo) {
    this.timeVaryingChannelRepo = timeVaryingChannelRepo;
  }

  @Override
  public void action(InteractionTaskRequest request) {
    Optional<Snowflake> guildId = request.getEvent().getInteraction().getGuildId();

    Long channelId = request.getOptionValue("channel")
        .as(Long.class);
    String defaultName = request.getOptionValue("default")
        .as(String.class);

    TimeVaryingChannelEntity timeVaryingChannel = this.timeVaryingChannelRepo.findByguildId(guildId.get().asLong()).orElse(new TimeVaryingChannelEntity(channelId, guildId.get().asLong(), defaultName));
    timeVaryingChannel.setChannelId(channelId);

    this.timeVaryingChannelRepo.save(timeVaryingChannel);
    request.getEvent().replyEphemeral(this.PERSIST_MESSAGE).block();
  }

  @Override
  public boolean canHandle(String command) {
    return command.equals("timechannel/set");
  }
}
