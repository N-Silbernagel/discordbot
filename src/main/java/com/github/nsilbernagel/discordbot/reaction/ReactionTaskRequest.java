package com.github.nsilbernagel.discordbot.reaction;

import com.github.nsilbernagel.discordbot.task.TaskRequest;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReactionTaskRequest extends TaskRequest {
  /**
   * The Message associated with the task, e.g. the message which was reacted on
   */
  private final Message message;

  /**
   * The TextChannel associated with the task, e.g. the TextChannel reaction was received on
   */
  private final TextChannel channel;

  /**
   * The Member associated with the task, e.g. the Member who reacted
   */
  private final Member author;
}
