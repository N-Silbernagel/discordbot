package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.interaction.InteractionTask;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.Channel;
import discord4j.rest.util.Permission;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@NeedsPermission(Permission.ADMINISTRATOR)
public class TimeVaryingChannelSetInteractionTask extends InteractionTask {
  public final static String PERSIST_MESSAGE = "Kanal gespeichert.";
  public final static String WRONG_TYPE_MESSAGE = "Der kanal muss ein Sprachkanal sein.";

  private final TimeVaryingChannelRepo timeVaryingChannelRepo;
  private final GatewayDiscordClient discordClient;

  public TimeVaryingChannelSetInteractionTask(TimeVaryingChannelRepo timeVaryingChannelRepo, GatewayDiscordClient discordClient) {
    this.timeVaryingChannelRepo = timeVaryingChannelRepo;
    this.discordClient = discordClient;
  }

  @Override
  public void action(InteractionTaskRequest request) {
    Optional<Snowflake> guildId = request.getEvent().getInteraction().getGuildId();

    Long channelId = request.getOptionValue("channel").as(Long.class);
    String defaultName = request.getOptionValue("default").as(String.class);

    Optional<String> morningName = Optional.ofNullable(request.getOptionValue("morning").as(String.class));
    Optional<String> noonName = Optional.ofNullable(request.getOptionValue("noon").as(String.class));
    Optional<String> eveningName = Optional.ofNullable(request.getOptionValue("evening").as(String.class));

    Channel givenChannel = discordClient.getChannelById(Snowflake.of(channelId)).block();

    assert givenChannel != null;

    if(!givenChannel.getType().equals(Channel.Type.GUILD_VOICE)) {
      request.getEvent().replyEphemeral(WRONG_TYPE_MESSAGE).block();
      return;
    }

    TimeVaryingChannelEntity timeVaryingChannel = this.timeVaryingChannelRepo.findByguildId(guildId.get().asLong()).orElse(new TimeVaryingChannelEntity(channelId, guildId.get().asLong(), defaultName));
    timeVaryingChannel.setChannelId(channelId);
    timeVaryingChannel.setMorningName(morningName.orElse(null));
    timeVaryingChannel.setNoonName(noonName.orElse(null));
    timeVaryingChannel.setEveningName(eveningName.orElse(null));

    this.timeVaryingChannelRepo.save(timeVaryingChannel);
    request.getEvent().replyEphemeral(PERSIST_MESSAGE).block();
  }

  @Override
  public boolean canHandle(String command) {
    return command.equals("timechannel/set");
  }
}
