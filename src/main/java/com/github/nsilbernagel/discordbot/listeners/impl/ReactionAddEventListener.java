package com.github.nsilbernagel.discordbot.listeners.impl;

import java.util.List;

import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.listeners.AbstractEventListener;
import com.github.nsilbernagel.discordbot.reaction.AbstractReactionTask;
import com.github.nsilbernagel.discordbot.reaction.ReactionTaskException;
import com.github.nsilbernagel.discordbot.reaction.ReactionToTaskHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;

@Component
public class ReactionAddEventListener extends AbstractEventListener<ReactionAddEvent> {
  @Value("${app.discord.command-token:!}")
  private String commandToken;

  @Autowired
  private ReactionToTaskHandler reactionToTaskHandler;

  @Autowired
  private ChannelBlacklist channelBlacklist;

  @Autowired
  private ExclusiveBotChannel exclusiveBotChannel;

  @Getter
  private Message message;

  @Getter
  private TextChannel messageChannel;

  @Getter
  private Member msgAuthor;

  @Getter
  private ReactionEmoji emoji;

  @Override
  public Class<ReactionAddEvent> getEventType() {
    return ReactionAddEvent.class;
  }

  @Override
  public void execute(ReactionAddEvent event) {
    this.message = event.getMessage().block();
    this.emoji = event.getEmoji();

    try {
      this.messageChannel = (TextChannel) this.message.getChannel().block();
    } catch (ClassCastException e) {
      // probably using a private channel which we dont support yet
      return;
    }

    if (!this.channelBlacklist.canAnswerOnChannel(this.messageChannel)) {
      return;
    }

    if (!this.exclusiveBotChannel.isOnExclusiveChannel(message)) {
      // just don't handle the event
      return;
    }

    List<AbstractReactionTask> tasks = this.reactionToTaskHandler.getReactionTasks(this.emoji);

    tasks.forEach(task -> {
      try {
        task.action();
      } catch (ReactionTaskException taskException) {
        // no error handling to reactiions for now, dont spam the text channel for
        // simple reactions
        // might need to add it for future applications
      }
    });
  }
}