package com.github.nsilbernagel.discordbot.message;

import discord4j.core.object.entity.Message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import discord4j.core.object.reaction.ReactionEmoji;

@Component
public class MessageToTaskHandler {

  @Value("${app.discord.command-token:!}")
  private String commandToken;

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
  public List<IMessageTask> getMessageTasks(Message message) {
    if (!message.getAuthor().isPresent() || message.getAuthor().get().isBot()) {
      return new ArrayList<IMessageTask>();
    }

    String messageContent = message.getContent().toLowerCase();
    if (!messageContent.startsWith(commandToken)) {
      return new ArrayList<IMessageTask>();
    }

    String keyword;
    messageContent = messageContent.replaceFirst(commandToken, "");
    int firstWhitespace = messageContent.indexOf(" ");
    if (firstWhitespace == (-1)) {
      keyword = messageContent;
    } else {
      keyword = messageContent.substring(0, (firstWhitespace));
    }

    List<IMessageTask> tasks = getTasksForKeyword(keyword);

    if (tasks.isEmpty()) {
      // react to members message with question mark emoji to show that the command
      // was not found
      message.addReaction(ReactionEmoji.unicode("\u2753")).block();
      return new ArrayList<IMessageTask>();
    }

    return tasks;
  }
}
