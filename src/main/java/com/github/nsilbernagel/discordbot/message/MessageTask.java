package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;

import org.springframework.beans.factory.annotation.Autowired;

import discord4j.core.object.entity.Message;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.Optional;

abstract public class MessageTask {
  @Autowired
  @Getter
  protected MessageToTaskHandler messageToTaskHandler;

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  protected Message getMessage() {
    return this.messageToTaskHandler.getMessage();
  }

  /**
   * Answer the message with a given text on the same channel
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

    Optional<Boolean> authorHasRequiredPermission = Optional.ofNullable(this.messageCreateEventListener.getMsgAuthor()
        .getBasePermissions()
        .flatMap(permissions -> Mono.just(permissions.contains(needsPermissionAnnotation.value())))
        .block());

    if (authorHasRequiredPermission.isPresent() && authorHasRequiredPermission.get()) {
      this.action();
    } else {
      Emoji.GUARD.reactOn(this.getMessage()).block();
    }
  }

  /*
   * Start the task that was triggered by a command in a channel.
   */
  abstract protected void action();

  /**
   * Check if a task can do anything with a given command keyword
   *
   * @param keyword the keyword to check
   * @return can handle keyword
   */
  abstract public boolean canHandle(String keyword);
}
