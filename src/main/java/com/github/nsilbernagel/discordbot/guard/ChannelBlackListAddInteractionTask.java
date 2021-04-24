package com.github.nsilbernagel.discordbot.guard;

import com.github.nsilbernagel.discordbot.guild.GuildRepo;
import com.github.nsilbernagel.discordbot.interaction.InteractionTask;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.rules.Required;
import org.springframework.stereotype.Component;

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
    String animal = request.getOptionValue("channel")
        .as(String.class);

    request.getEvent().replyEphemeral(animal).block();
  }

  @Override
  public boolean canHandle(String command) {
    return command.equals("blacklist/add");
  }
}
