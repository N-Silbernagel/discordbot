package com.github.nsilbernagel.discordbot.message;

import discord4j.core.object.entity.Message;

import java.util.Arrays;
import java.util.Optional;

public class MessageToTaskHandler {

    /*
     * Get the right task implementation depending on the keyword that was used.
     */
    public static Optional<IMessageTask> getMessageTask(Message message) {
        if (message.getAuthor().isPresent()) {
            if (message.getAuthor().get().isBot()) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
        String messageContent = message.getContent().toLowerCase();
        if (messageContent.startsWith("!")) {
            String keyword;
            String additionalInfo = "";
            messageContent = messageContent.replaceFirst("!", "");
            int firstWhitespace = messageContent.indexOf(" ");
            if (firstWhitespace == (-1)) {
                keyword = messageContent;
            } else {
                keyword = messageContent.substring(0, (firstWhitespace));
                additionalInfo = messageContent.substring(firstWhitespace + 1);
            }
            Optional<EMessageToTaskMapper> task = Arrays.stream(EMessageToTaskMapper.values())
                    .filter(messageTask -> messageTask.getMessageKey().equals(keyword))
                    .findFirst();
            if (task.isPresent()) {
                CommandPattern pattern = new CommandPattern(keyword, additionalInfo);
                return Optional.of(task.get().getTask(message, pattern));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
