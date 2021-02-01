package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.IMessageTask;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.HtmlUtils;

import discord4j.core.object.entity.Message;

@Component
public class InsultTask extends AbstractMessageTask implements IMessageTask {

  public final static String KEYWORD = "beleidige";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void execute(Message message) {
    this.message = message;

    String insult = WebClient.create("https://evilinsult.com")
        .get()
        .uri("/generate_insult.php?lang=en&type=text")
        .retrieve()
        .bodyToMono(String.class)
        .onErrorReturn("Dicke Qualle")
        .block();

    this.answerMessage(HtmlUtils.htmlUnescape(insult));
  }
}
