package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.message.MessageCreateTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.TaskException;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.HtmlUtils;

@Component
public class InsultTask extends MessageCreateTask implements ExplainedMessageTask {

  public final static String KEYWORD = "insult";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword) || keyword.equals("beleidige");
  }

  @Override
  public void action(MsgTaskRequest taskRequest) {
    String insult = WebClient.create("https://evilinsult.com")
        .get()
        .uri("/generate_insult.php?lang=en&type=text")
        .retrieve()
        .bodyToMono(String.class)
        .doOnError(err -> {
          throw new TaskException("Dicke Qualle", err);
        })
        .block();

    taskRequest.respond(HtmlUtils.htmlUnescape(insult)).block();
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Einen Nutzer beleidigen.";
  }
}
