package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.CommandPattern;
import com.github.nsilbernagel.discordbot.message.FalseInputException;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import discord4j.core.object.entity.Message;

import java.util.Random;

public class RandomNumberGeneratorTask extends AbstractMessageTask implements IMessageTask {

  public static final String KEYWORD = "dice";

  public RandomNumberGeneratorTask(Message message, CommandPattern pattern) {
    super(message, pattern);
  }

  @Override
  public void execute() {

  }

  private int getRandomNumber(int lowerBorder, int upperBorder) {
    Random random = new Random();
    if (lowerBorder >= upperBorder) {
      throw new FalseInputException("Upper Border cannot be greater than lower Border!");
    }
    return random.nextInt(upperBorder - lowerBorder);
  }

  public static String getKeyword() {
    return KEYWORD;
  }
}
