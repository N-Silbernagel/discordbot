package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;

import com.github.nsilbernagel.discordbot.task.Task;
import com.github.nsilbernagel.discordbot.task.TaskRequest;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Optional;

abstract public class MessageTask extends Task {
  protected final ThreadLocal<TaskRequest> msgTaskRequest = new ThreadLocal<>();

  /**
   * Answer the message with a given text on the same channel
   */
  public Mono<Message> answerMessage(String answerText) {
    return this.msgTaskRequest.get()
        .getChannel()
        .createMessage(answerText);
  }

  /**
   * Get the message that initiated the message task
   */
  protected Message currentMessage() {
    return this.msgTaskRequest.get().getMessage();
  }

  /**
   * Get the channel that the current task was initiated on
   */
  protected TextChannel currentChannel() {
    return this.msgTaskRequest.get().getChannel();
  }

  /**
   * Get the author that initiated the message task
   */
  protected Member currentAuthor() {
    return this.msgTaskRequest.get().getAuthor();
  }

  /**
   * Execute the message task action considering the needed permissions
   */
  public void execute(TaskRequest taskRequest) {
    this.msgTaskRequest.set(taskRequest);
    NeedsPermission needsPermissionAnnotation = this.getClass().getAnnotation(NeedsPermission.class);

    if (needsPermissionAnnotation == null) {
      this.action();
      return;
    }

    Optional<Boolean> authorHasRequiredPermission = Optional.ofNullable(this.msgTaskRequest.get().getAuthor()
        .getBasePermissions()
        .flatMap(permissions -> Mono.just(permissions.contains(needsPermissionAnnotation.value())))
        .block());

    if (authorHasRequiredPermission.isPresent() && authorHasRequiredPermission.get()) {
      this.action();
    } else {
      Emoji.GUARD.reactOn(this.msgTaskRequest.get().getMessage()).block();
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
