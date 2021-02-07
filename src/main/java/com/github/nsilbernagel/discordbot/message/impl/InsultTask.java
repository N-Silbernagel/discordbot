package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.TaskException;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.HtmlUtils;

@Component
public class InsultTask extends AbstractMessageTask {

  public final static String KEYWORD = "beleidige";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    String insult = WebClient.create("https://evilinsult.com")
        .get()
        .uri("/generate_insult.php?lang=en&type=text")
        .retrieve()
        .bodyToMono(String.class)
        .doOnError(err -> {
          throw new TaskException("Dicke Qualle", err);
        })
        .block();

    this.answerMessage(HtmlUtils.htmlUnescape(insult)).block();
  }
}
