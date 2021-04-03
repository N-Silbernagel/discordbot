package com.github.nsilbernagel.discordbot.reaction;

import java.util.ArrayList;
import java.util.List;

import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.listener.EventListener;
import com.github.nsilbernagel.discordbot.task.MemberMissingOrBotException;
import com.github.nsilbernagel.discordbot.task.TaskException;

import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;

@Component
public class ReactionAddEventListener extends EventListener<ReactionAddEvent> {

  private final ChannelBlacklist channelBlacklist;
  private final ExclusiveBotChannel exclusiveBotChannel;
  private final List<ReactionTask> tasks;

  private final ThreadLocal<ReactionTaskRequest> taskRequest = new ThreadLocal<>();

  public ReactionAddEventListener(ChannelBlacklist channelBlacklist, ExclusiveBotChannel exclusiveBotChannel, List<ReactionTask> tasks) {
    this.channelBlacklist = channelBlacklist;
    this.exclusiveBotChannel = exclusiveBotChannel;
    this.tasks = tasks;
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
          new ReactionTaskRequest(
              event.getMessage().block(),
              (TextChannel) event.getChannel().block(),
              event.getMember().get()
          )
      );
    } catch (ClassCastException e) {
      // probably using a private channel which we dont support yet
      return;
    }

    if (!this.channelBlacklist.canAnswerOnChannel(this.taskRequest.get().getChannel())) {
      return;
    }

    if (!this.exclusiveBotChannel.isOnExclusiveChannel(this.taskRequest.get().getMessage())) {
      // just don't handle the event
      return;
    }

    List<ReactionTask> tasks = this.getTasksForReactionEmoji(event.getEmoji());

    tasks.forEach(task ->
        task.execute(this.taskRequest.get())
    );
  }

  /**
   * Get tasks that can handle a given keyword
   */
  private List<ReactionTask> getTasksForReactionEmoji(ReactionEmoji reactionEmoji) {
    List<ReactionTask> result = new ArrayList<>();
    for (ReactionTask task : tasks) {
      if (task.canHandle(reactionEmoji)) {
        result.add(task);
      }
    }

    return result;
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