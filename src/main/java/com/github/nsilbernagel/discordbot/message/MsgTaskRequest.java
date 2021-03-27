package com.github.nsilbernagel.discordbot.message;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.Data;

/**
 * data passed to message tasks
 */
@Data
public class MsgTaskRequest {
  /**
   * The message which initiated the task request
   */
  private final Message message;

  /**
   * the text channel the message was written on
   */
  private final TextChannel channel;

  /**
   * The Author of the message
   */
  private final Member author;
}
