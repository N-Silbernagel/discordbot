package com.github.nsilbernagel.discordbot.interaction;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.task.Task;
import discord4j.core.event.domain.InteractionCreateEvent;

import java.util.Optional;

abstract public class InteractionTask extends Task {
  public void execute(InteractionTaskRequest request){
    if(!this.authorHasRequiredPermission(request)){
      request.getEvent().replyEphemeral("Du bist nicht berechtigt diesen Kommando zu verwenden.").block();
      return;
    }

    this.action(request);
  }

  private boolean authorHasRequiredPermission(InteractionTaskRequest request) {
    NeedsPermission needsPermissionAnnotation = this.getClass().getAnnotation(NeedsPermission.class);

    if(needsPermissionAnnotation == null){
      return true;
    }

    Optional<Boolean> memberHasRequiredPermission = Optional.ofNullable(request.getEvent()
        .getInteraction()
        .getMember()
        .get()
        .getBasePermissions()
        .map(permissions -> permissions.contains(needsPermissionAnnotation.value()))
        .block());

    return memberHasRequiredPermission.isPresent() && memberHasRequiredPermission.get();
  }

  abstract public void action(InteractionTaskRequest request);

  abstract public boolean canHandle(String command);
}
