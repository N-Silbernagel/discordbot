package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.interaction.InteractionTask;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.Channel;
import discord4j.rest.util.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@NeedsPermission(Permission.ADMINISTRATOR)
public class TimeVaryingChannelSetInteractionTask extends InteractionTask {
  public final String PERSIST_MESSAGE = "Channel set.";
  public final String WRONG_TYPE_MESSAGE = "Channel must be a guild's text or voice channel.";

  private final TimeVaryingChannelRepo timeVaryingChannelRepo;
  private final GatewayDiscordClient discordClient;

  public TimeVaryingChannelSetInteractionTask(TimeVaryingChannelRepo timeVaryingChannelRepo, GatewayDiscordClient discordClient) {
    this.timeVaryingChannelRepo = timeVaryingChannelRepo;
    this.discordClient = discordClient;
  }

  @Override
  public void action(InteractionTaskRequest request) {
    Optional<Snowflake> guildId = request.getEvent().getInteraction().getGuildId();

    Long channelId = request.getOptionValue("channel")
        .as(Long.class);
    String defaultName = request.getOptionValue("default")
        .as(String.class);

    Channel givenChannel = discordClient.getChannelById(Snowflake.of(channelId)).block();

    if(!List.of(Channel.Type.GUILD_TEXT, Channel.Type.GUILD_VOICE).contains(givenChannel.getType())) {
      request.getEvent().replyEphemeral(WRONG_TYPE_MESSAGE).block();
      return;
    }

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
