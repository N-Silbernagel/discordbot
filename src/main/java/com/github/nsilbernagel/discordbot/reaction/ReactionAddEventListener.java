package com.github.nsilbernagel.discordbot.reaction;

import java.util.List;

import com.github.nsilbernagel.discordbot.communication.Emoji;
import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.listener.EventListener;
import com.github.nsilbernagel.discordbot.message.TaskException;

import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;

@Component
public class ReactionAddEventListener extends EventListener<ReactionAddEvent> {

  private final ReactionToTaskHandler reactionToTaskHandler;
  private final ChannelBlacklist channelBlacklist;
  private final ExclusiveBotChannel exclusiveBotChannel;

  @Getter
  private Message message;

  @Getter
  private TextChannel messageChannel;

  @Getter
  private ReactionAddEvent reactionAddEvent;

  @Getter
  private ReactionEmoji emoji;

  public ReactionAddEventListener(ReactionToTaskHandler reactionToTaskHandler, ChannelBlacklist channelBlacklist, ExclusiveBotChannel exclusiveBotChannel) {
    this.reactionToTaskHandler = reactionToTaskHandler;
    this.channelBlacklist = channelBlacklist;
    this.exclusiveBotChannel = exclusiveBotChannel;
  }

  @Override
  public Class<ReactionAddEvent> getEventType() {
    return ReactionAddEvent.class;
  }

  @Override
  public void execute(ReactionAddEvent event) {
    if (event.getMember().isEmpty() || event.getMember().get().isBot()) {
      return;
    }

    this.reactionAddEvent = event;

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

    List<ReactionTask> tasks = this.reactionToTaskHandler.getReactionTasks(this.emoji);

    tasks.forEach(ReactionTask::action);
  }

  protected void onCheckedException(TaskException exception) {
    // no error handling to reactions for now, dont spam the text channel for
    // simple reactions
    // might need to add it for future applications
  }

  protected void onUncheckedException(Exception uncheckedException) {
    Emoji.BUG.reactOn(this.message).subscribe();
  }
}