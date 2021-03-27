package com.github.nsilbernagel.discordbot.reaction;

import java.util.List;

import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.listener.EventListener;
import com.github.nsilbernagel.discordbot.message.MemberMissingOrBotException;
import com.github.nsilbernagel.discordbot.message.TaskException;

import com.github.nsilbernagel.discordbot.message.TaskRequest;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;

@Component
public class ReactionAddEventListener extends EventListener<ReactionAddEvent> {

  private final ReactionToTaskHandler reactionToTaskHandler;
  private final ChannelBlacklist channelBlacklist;
  private final ExclusiveBotChannel exclusiveBotChannel;

  @Getter
  private ReactionEmoji emoji;

  private final ThreadLocal<TaskRequest> taskRequest = new ThreadLocal<>();

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
      throw new MemberMissingOrBotException();
    }

    try {
      this.taskRequest.set(
          new TaskRequest(
              event.getMessage().block(),
              (TextChannel) event.getChannel().block(),
              event.getMember().get()
          )
      );
    } catch (ClassCastException e) {
      // probably using a private channel which we dont support yet
      return;
    }

    this.emoji = event.getEmoji();

    if (!this.channelBlacklist.canAnswerOnChannel(this.taskRequest.get().getChannel())) {
      return;
    }

    if (!this.exclusiveBotChannel.isOnExclusiveChannel(this.taskRequest.get().getMessage())) {
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
    Emoji.BUG.reactOn(this.taskRequest.get().getMessage()).subscribe();
  }
}