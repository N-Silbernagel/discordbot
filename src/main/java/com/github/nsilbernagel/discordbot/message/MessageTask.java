package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;

import com.github.nsilbernagel.discordbot.task.Task;

import reactor.core.publisher.Mono;

import java.util.Optional;

abstract public class MessageTask extends Task {
  /**
   * Execute the message task action considering the needed permissions
   */
  public void execute(MsgTaskRequest taskRequest) {

    if(!this.authorHasRequiredPermission(taskRequest)){
      Emoji.GUARD.reactOn(taskRequest.getMessage()).block();
      return;
    }

    this.action(taskRequest);
  }

  private boolean authorHasRequiredPermission(MsgTaskRequest taskRequest) {
    NeedsPermission needsPermissionAnnotation = this.getClass().getAnnotation(NeedsPermission.class);

    if(needsPermissionAnnotation == null){
      return true;
    }

    Optional<Boolean> authorHasRequiredPermission = Optional.ofNullable(taskRequest.getAuthor()
        .getBasePermissions()
        .flatMap(permissions -> Mono.just(permissions.contains(needsPermissionAnnotation.value())))
        .block());

    return authorHasRequiredPermission.isPresent() && authorHasRequiredPermission.get();
  }

  /*
   * Start the task that was triggered by a command in a channel.
   */
  abstract protected void action(MsgTaskRequest taskRequest);

  /**
   * Check if a task can do anything with a given command
   */
  abstract public boolean canHandle(String command);
}
