package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.impl.NilsTask;
import com.github.nsilbernagel.discordbot.message.impl.PongTask;
import com.github.nsilbernagel.discordbot.message.impl.RandomNumberGeneratorTask;
import com.github.nsilbernagel.discordbot.message.impl.VoteKickTask;

import discord4j.core.object.entity.Message;
import lombok.Getter;

public enum EMessageToTaskMapper {

  PONG(PongTask.getKeyword()) {
    @Override
    public IMessageTask getTask(Message message) {
      return new PongTask(message);
    }
  },

  NILS(NilsTask.getKeyword()) {
    @Override
    public IMessageTask getTask(Message message) {
      return new NilsTask(message);
    }
  },

  DICE(RandomNumberGeneratorTask.getKeyword()) {
    @Override
    public IMessageTask getTask(Message message) {
      return new RandomNumberGeneratorTask(message);
    }
  },

  VOTEKICK(VoteKickTask.getKeyword()) {
    @Override
    public IMessageTask getTask(Message message) {
      return new VoteKickTask(message);
    }
  };

  @Getter
  private final String messageKey; // Keyword that shall map the command to the write IMessageTask.

  EMessageToTaskMapper(String messageKey) {
    this.messageKey = messageKey;
  }

  public abstract IMessageTask getTask(Message message);
}