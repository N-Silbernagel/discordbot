package com.github.nsilbernagel.discordbot.reaction;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.reaction.ReactionEmoji;

@Component
public class ReactionToTaskHandler {
  private final List<ReactionTask> tasks;

  public ReactionToTaskHandler(List<ReactionTask> tasks) {
    this.tasks = tasks;
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

  /*
   * Get the right task implementation depending on the keyword that was used.
   */
  public List<ReactionTask> getReactionTasks(ReactionEmoji reactionEmoji) {

    List<ReactionTask> tasks = getTasksForReactionEmoji(reactionEmoji);

    if (tasks.isEmpty()) {
      return new ArrayList<>();
    }

    return tasks;
  }
}
