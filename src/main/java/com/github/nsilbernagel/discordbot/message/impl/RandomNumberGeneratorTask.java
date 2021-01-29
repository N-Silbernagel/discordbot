package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.FalseInputException;
import com.github.nsilbernagel.discordbot.message.IMessageTask;

import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Message;

import java.util.Random;

@Component
public class RandomNumberGeneratorTask extends AbstractMessageTask implements IMessageTask {

  public static final String KEYWORD = "dice";

  @Override
  public void execute(Message message) {
    this.message = message;
  }

  private int getRandomNumber(int lowerBorder, int upperBorder) {
    Random random = new Random();
    if (lowerBorder >= upperBorder) {
      throw new FalseInputException("Upper Border cannot be greater than lower Border!");
    }
    return random.nextInt(upperBorder - lowerBorder);
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}
