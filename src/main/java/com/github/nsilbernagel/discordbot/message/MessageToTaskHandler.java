package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import discord4j.core.object.entity.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
public class MessageToTaskHandler {

  @Value("${app.discord.command-token:!}")
  private String commandToken;

  @Autowired
  private List<MessageTask> tasks;

  @Getter
  private Message message;

  @Getter
  private String command;

  @Getter
  private List<String> commandParameters;

  /**
   * Get tasks that can handle a given keyword
   */
  private List<MessageTask> getTasksForKeyword() {
    List<MessageTask> result = new ArrayList<>();
    for (MessageTask task : tasks) {
      if (task.canHandle(this.command)) {
        result.add(task);
      }
    }

    return result;
  }

  /*
   * Get the right task implementation depending on the keyword that was used.
   */
  public List<MessageTask> getMessageTasks(Message message) {
    this.message = message;

    String messageContent = message.getContent().replaceFirst(commandToken, "");
    List<String> messageParts = Arrays.asList(messageContent.split(" "));

    this.command = messageParts.get(0).toLowerCase();
    if (messageParts.size() > 1) {
      this.commandParameters = messageParts.subList(1, messageParts.size());
    } else {
      this.commandParameters = new ArrayList<String>();
    }

    List<MessageTask> tasks = getTasksForKeyword();

    if (tasks.isEmpty()) {
      // react to members message with question mark emoji to show that the command
      // was not found
      Emoji.QUESTION_MARK.reactOn(message).block();
      return new ArrayList<>();
    }

    return tasks;
  }
}
