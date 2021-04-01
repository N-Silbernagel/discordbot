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
  /**
   * Execute the message task action considering the needed permissions
   */
  public void execute(MsgTaskRequest taskRequest) {
    NeedsPermission needsPermissionAnnotation = this.getClass().getAnnotation(NeedsPermission.class);

    if (needsPermissionAnnotation == null) {
      this.action(taskRequest);
      return;
    }

    Optional<Boolean> authorHasRequiredPermission = Optional.ofNullable(taskRequest.getAuthor()
        .getBasePermissions()
        .flatMap(permissions -> Mono.just(permissions.contains(needsPermissionAnnotation.value())))
        .block());

    if (authorHasRequiredPermission.isPresent() && authorHasRequiredPermission.get()) {
      this.action(taskRequest);
    } else {
      Emoji.GUARD.reactOn(taskRequest.getMessage()).block();
    }
  }

  /*
   * Start the task that was triggered by a command in a channel.
   */
  abstract protected void action(MsgTaskRequest taskRequest);

  /**
   * Check if a task can do anything with a given command keyword
   *
   * @param keyword the keyword to check
   * @return can handle keyword
   */
  abstract public boolean canHandle(String keyword);
}
