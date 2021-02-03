package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import discord4j.core.object.entity.Message;

@Component
public class RandomRedditPostTask extends AbstractMessageTask implements IMessageTask {

  public static final String KEYWORD = "reddit";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  public String getRandomSubreddit() {
    return WebClient.create("https://www.reddit.com")
        .get()
        .uri("/r/random")
        .exchange()
        .map(res -> res.headers()
            .asHttpHeaders()
            .getLocation()
            .normalize()
            .getPath()
            .split("/")[2])
        .doOnError(err -> {
          throw new TaskException("Ich konnte leider keinen Subreddit für dich finden", err);
        })
        .block();
  }

  public String getRandomPost() {
    return WebClient.create("https://www.reddit.com")
        .get()
        .uri("/r/" + this.getRandomSubreddit() + "/random.json")
        .exchange()
        .map(res -> res.headers()
            .asHttpHeaders()
            .getLocation()
            .normalize()
            .toString()
            .split(".json")[0])
        .doOnError(err -> {
          throw new TaskException("Ich konnte leider keinen Post für dich finden", err);
        })
        .block();
  }

  @Override
  public void execute(Message message) {
    this.message = message;

    this.answerMessage(this.getRandomPost());
  }
}
