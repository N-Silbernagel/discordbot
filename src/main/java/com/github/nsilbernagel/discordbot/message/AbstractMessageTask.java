package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import reactor.core.publisher.Mono;

abstract public class AbstractMessageTask {
  @Autowired
  protected MessageToTaskHandler messageToTaskHandler;

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  protected Message getMessage() {
    return this.messageToTaskHandler.getMessage();
  }

  /**
   * Answer the message with a given text on the same channel
   *
   * @param answerText
   */
  public Mono<Message> answerMessage(String answerText) {
    return this.messageCreateEventListener.getMessageChannel()
        .createMessage(answerText);
  }

  /**
   * Execute the message task action considering the needed permissions
   */
  public void execute() {
    NeedsPermission needsPermissionAnnotation = this.getClass().getAnnotation(NeedsPermission.class);

    if (needsPermissionAnnotation == null) {
      this.action();
      return;
    }

    boolean authorHasRequiredPermission = this.messageToTaskHandler.getMsgAuthor()
        .getBasePermissions()
        .flatMap(permissions -> Mono.just(permissions.contains(needsPermissionAnnotation.value())))
        .block();

    if (!authorHasRequiredPermission) {
      this.getMessage()
          .addReaction(ReactionEmoji.unicode("👮‍♂️"))
          .block();
    } else {
      this.action();
    }
  }

  /*
   * Start the task that was triggered by a command in a channel.
   */
  abstract protected void action();

  /**
   * Check if a task can do anything with a given command keyword
   *
   * @param keyword
   *                  the keyword to check
   * @return can handle keyword
   */
  abstract public boolean canHandle(String keyword);
}
