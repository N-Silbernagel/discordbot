package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.TaskLogicException;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

abstract class AbstractMessageTask {
  final Message message;

  public AbstractMessageTask(Message message) {
    this.message = message;
  }

  /**
   * Answer the message with a given text on the same channel
   *
   * @param answerText
   */
  public void answerMessage(String answerText) {
    message.getChannel().flatMap(messageChannel -> messageChannel.createMessage(answerText)).subscribe();
  }

  /**
   * Get user as member of the given guild
   *
   * @param user
   * @param guildId
   * @return the users member representation
   * @throws TaskLogicException
   */
  public Member userAsMemberOfGuild(User user, Snowflake guildId) throws TaskLogicException {
    return user.asMember(guildId).blockOptional()
        .orElseThrow(() -> new TaskLogicException(user.getUsername() + " ist kein Member dieses Servers"));
  }
}
