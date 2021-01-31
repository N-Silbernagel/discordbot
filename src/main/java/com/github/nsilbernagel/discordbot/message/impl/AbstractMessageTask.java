package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.TaskException;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

abstract class AbstractMessageTask {
  public Message message;

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
   * @throws TaskException
   */
  public Member userAsMemberOfGuild(User user, Snowflake guildId) throws TaskException {
    return user.asMember(guildId).blockOptional()
        .orElseThrow(() -> new TaskException(user.getUsername() + " ist kein Member dieses Servers"));
  }
}
