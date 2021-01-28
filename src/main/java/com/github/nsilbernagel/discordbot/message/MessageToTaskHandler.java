package com.github.nsilbernagel.discordbot.message;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.Arrays;
import java.util.Optional;

public class MessageToTaskHandler {
  /*
   * Get the right task implementation depending on the keyword that was used.
   */
  public static Optional<IMessageTask> getMessageTask(Message message) {
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
    Optional<EMessageToTaskMapper> task = Arrays.stream(EMessageToTaskMapper.values())
        .filter(messageTask -> messageTask.getMessageKey().equals(keyword)).findFirst();
    if (task.isPresent()) {
      return Optional.of(task.get().getTask(message));
    } else {
      // react to members message with question mark emoji to show that the command
      // was not found
      message.addReaction(ReactionEmoji.unicode("\u2753")).block();
      return Optional.empty();
    }
  }
}
