package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.task.TaskRequest;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MsgTaskRequest extends TaskRequest {
  /**
   * The Message associated with the task, e.g. the message containing the command for a MessageTask
   */
  private final Message message;

  /**
   * The TextChannel associated with the task, e.g. the TextChannel the message containing the command was written on for a MessageTask
   */
  private final TextChannel channel;

  /**
   * The Member associated with the task, e.g. the Member who wrote the message containing the command for a MessageTask
   */
  private final Member author;
}
