package com.github.nsilbernagel.discordbot.message;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageToTaskHandler {

  @Autowired
  private List<IMessageTask> tasks;

  /**
   * Get tasks that can handle a given keywork
   */
  private List<IMessageTask> getTasksForKeyword(String key) {
    List<IMessageTask> result = new ArrayList<>();
    for (IMessageTask task : tasks) {
      if (task.canHandle(key)) {
        result.add(task);
      }
    }

    return result;
  }

  /*
   * Get the right task implementation depending on the keyword that was used.
   */
  public Optional<IMessageTask> getMessageTask(Message message) {
    if (!message.getAuthor().isPresent() || message.getAuthor().get().isBot()) {
      return Optional.empty();
    }

    String messageContent = message.getContent().toLowerCase();
    if (!messageContent.startsWith("!")) {
      return Optional.empty();
    }

    String keyword;
    messageContent = messageContent.replaceFirst("!", "");
    int firstWhitespace = messageContent.indexOf(" ");
    if (firstWhitespace == (-1)) {
      keyword = messageContent;
    } else {
      keyword = messageContent.substring(0, (firstWhitespace));
    }

    List<IMessageTask> tasks = getTasksForKeyword(keyword);
    // TODO: execute mutiple tasks that can handle a keyword, not just first
    Optional<IMessageTask> task = tasks.stream()
        .filter(messageTask -> messageTask.canHandle(keyword)).findFirst();
    if (task.isPresent()) {
      return task;
    } else {
      // react to members message with question mark emoji to show that the command
      // was not found
      message.addReaction(ReactionEmoji.unicode("\u2753")).block();
      return Optional.empty();
    }
  }
}
