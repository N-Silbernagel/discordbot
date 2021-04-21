package com.github.nsilbernagel.discordbot.guard;

import com.github.nsilbernagel.discordbot.guild.GuildEntity;
import com.github.nsilbernagel.discordbot.guild.GuildRepo;
import com.github.nsilbernagel.discordbot.interaction.InteractionTask;
import discord4j.core.event.domain.InteractionCreateEvent;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ChannelBlackListInteractionTask extends InteractionTask {
  private final GuildRepo guildRepo;
  private final ChannelBlacklistRepo channelBlacklistRepo;

  public ChannelBlackListInteractionTask(GuildRepo guildRepo, ChannelBlacklistRepo channelBlacklistRepo) {
    this.guildRepo = guildRepo;
    this.channelBlacklistRepo = channelBlacklistRepo;
  }

  @Override
  public void action(InteractionCreateEvent event) {
    if (event.getInteraction().getData().data().get().options().get().get(0).name().equals("add")) {
      GuildEntity guild = new GuildEntity(event.getInteraction().getGuildId().get().asLong());
      this.guildRepo.save(guild);

      BlackListedChannelEntity blackListedChannelEntity = new BlackListedChannelEntity(Long.parseLong(event.getInteraction().getData().data().get().resolved().get().channels().get().keySet().stream().collect(Collectors.toList()).get(0)), guild);
      this.channelBlacklistRepo.save(blackListedChannelEntity);
    } else if(event.getInteraction().getData().data().get().options().get().get(0).name().equals("remove")) {

    }
  }

  @Override
  public boolean canHandle(String command) {
    return command.equals("blacklist");
  }
}
