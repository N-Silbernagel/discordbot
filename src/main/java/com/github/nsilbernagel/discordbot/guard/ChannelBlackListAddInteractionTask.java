package com.github.nsilbernagel.discordbot.guard;

import com.github.nsilbernagel.discordbot.guild.GuildEntity;
import com.github.nsilbernagel.discordbot.guild.GuildRepo;
import com.github.nsilbernagel.discordbot.interaction.InteractionTask;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import discord4j.common.util.Snowflake;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChannelBlackListAddInteractionTask extends InteractionTask {
  private final GuildRepo guildRepo;
  private final ChannelBlacklistRepo channelBlacklistRepo;

  public ChannelBlackListAddInteractionTask(GuildRepo guildRepo, ChannelBlacklistRepo channelBlacklistRepo) {
    this.guildRepo = guildRepo;
    this.channelBlacklistRepo = channelBlacklistRepo;
  }

  @Override
  public void action(InteractionTaskRequest request) {
    long channelId = request.getOptionValue("channel")
        .as(Long.class);

    Optional<Snowflake> guildId = request.getEvent().getInteraction().getGuildId();

    if(guildId.isEmpty()){
      request.getEvent().replyEphemeral("Dieser Befehl kann nur auf Servern verwendet werden");
      return;
    }

    GuildEntity guild = guildRepo.findBydcId(guildId.get().asLong()).orElseGet(() -> {
      GuildEntity g = new GuildEntity(guildId.get().asLong());
      guildRepo.save(g);
      return g;
    });

    channelBlacklistRepo.findByChannelId(channelId).orElseGet(() -> {
      BlackListedChannelEntity c = new BlackListedChannelEntity(channelId, guild);
      channelBlacklistRepo.save(c);
      return c;
    });

    request.getEvent().replyEphemeral("Kanal wurde zur Blacklist hinzugefügt.").block();
  }

  @Override
  public boolean canHandle(String command) {
    return command.equals("blacklist/add");
  }
}
