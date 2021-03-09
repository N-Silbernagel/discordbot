package com.github.nsilbernagel.discordbot.reaction;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.reaction.ReactionEmoji;

@Component
public class ReactionToTaskHandler {
  @Autowired
  private List<AbstractReactionTask> tasks;

  /**
   * Get tasks that can handle a given keyword
   */
  private List<AbstractReactionTask> getTasksForReactionEmoji(ReactionEmoji reactionEmoji) {
    List<AbstractReactionTask> result = new ArrayList<>();
    for (AbstractReactionTask task : tasks) {
      if (task.canHandle(reactionEmoji)) {
        result.add(task);
      }
    }

    return result;
  }

  /*
   * Get the right task implementation depending on the keyword that was used.
   */
  public List<AbstractReactionTask> getReactionTasks(ReactionEmoji reactionEmoji) {

    List<AbstractReactionTask> tasks = getTasksForReactionEmoji(reactionEmoji);

    if (tasks.isEmpty()) {
      return new ArrayList<>();
    }

    return tasks;
  }
}
