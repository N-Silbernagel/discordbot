package com.github.nsilbernagel.discordbot.guard;

import com.github.nsilbernagel.discordbot.interaction.InteractionTask;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExclusiveChannelInteractionTask extends InteractionTask {

  private final ExclusiveChannelRepository exclusiveChannelRepository;
  private final GatewayDiscordClient gatewayDiscordClient;

  public ExclusiveChannelInteractionTask(ExclusiveChannelRepository exclusiveChannelRepository, GatewayDiscordClient gatewayDiscordClient) {
    this.exclusiveChannelRepository = exclusiveChannelRepository;
    this.gatewayDiscordClient = gatewayDiscordClient;
  }

  @Override
  public void action(InteractionTaskRequest request) {
    Long channelId = request.getOptionValue("channel")
        .as(Long.class);

    Optional<Snowflake> guildId = request.getEvent().getInteraction().getGuildId();
    if(guildId.isEmpty()){
      return;
    }

    Optional<ExclusiveChannelEntity> existingExclusiveChannel = this.exclusiveChannelRepository.findByguildId(guildId.get().asLong());

    if(channelId == null) {
      this.handleEmptyChannelId(request, existingExclusiveChannel);
      return;
    }

    Channel channel = this.gatewayDiscordClient.getChannelById(Snowflake.of(channelId)).block();
    if(!channel.getType().equals(Channel.Type.GUILD_TEXT)){
      request.getEvent().replyEphemeral("Bitte gib einen TextChannel eines Servers an.").block();
      return;
    }

    ExclusiveChannelEntity exclusiveChannelEntity = existingExclusiveChannel.orElse(new ExclusiveChannelEntity(channelId, guildId.get().asLong()));
    exclusiveChannelEntity.setChannelId(channelId);
    this.exclusiveChannelRepository.save(exclusiveChannelEntity);
    request.getEvent().replyEphemeral("Exklusiver Channel gesetzt.").block();
  }

  private void handleEmptyChannelId(InteractionTaskRequest request, Optional<ExclusiveChannelEntity> existingExclusiveChannel) {
    if(existingExclusiveChannel.isEmpty()) {
      request.getEvent().replyEphemeral("Es wurde noch kein exklusiver Textkanal festgelegt.").block();
      return;
    }

    this.exclusiveChannelRepository.delete(existingExclusiveChannel.get());
    request.getEvent().replyEphemeral("Exklusiver Channel zurückgesetzt.").block();
  }

  @Override
  public boolean canHandle(String command) {
    return command.equals("exclusivechannel");
  }
}
