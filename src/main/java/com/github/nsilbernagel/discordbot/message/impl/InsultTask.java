package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.HtmlUtils;

@Component
public class InsultTask extends MessageTask implements ExplainedMessageTask {

  public final static String KEYWORD = "insult";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword) || keyword.equals("beleidige");
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

  public String getKeyword() {
    return KEYWORD;
  };

  public String getExplaination() {
    return "Eine Nutzer beleidigen.";
  };
}
