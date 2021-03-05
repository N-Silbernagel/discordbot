package com.github.nsilbernagel.discordbot.message.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.CommandParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import net.ricecode.similarity.JaroWinklerStrategy;

@Component
public class SoundTask extends AbstractMessageTask implements ExplainedMessageTask {

  public static final String KEYWORD = "sound";

  @CommandParam(0)
  private String soundName;

  @Autowired
  private PlayTask playTask;

  @Autowired
  private GatewayDiscordClient discordClient;

  private JsonNode soundNode;

  private JsonNode awsmSounds;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {

    if (awsmSounds == null){
      throw new TaskException("awesome sounds not found :(");
    }

    if (soundName == null) {
      soundNode = awsmSounds.get(ThreadLocalRandom.current().nextInt(0, awsmSounds.size() + 1));
    } else {
      soundNode = getJsonByName(soundName);
    }

    playSound(soundNode);
    
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Einen Sound abspielen";
  }

  @PostConstruct
  public void getAWSMsounds() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      awsmSounds = mapper.readTree(
                    WebClient.create("https://sounds-backend.awsm.rocks")
                        .get()
                        .uri("/api/sounds")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("token", "ysOp8JjBAbFhMoDEWWRbMHBAYqEJOAopkFRQvHSogTIosB500tV3ZvjMaH8l1wUTosU3LwtQEzR8xZ7lcwwHsk0ymFtPgXHbDzQUuOFtRlVgnrZ9FqFgb9mq5x7Ifqe6")
                        .exchange()
                        .block()
                        .bodyToMono(String.class)
                        .block()
                        );
    } catch (Exception e) {
      awsmSounds = null;
    }
  }

  public JsonNode getJsonByName(String soundName) {
    JaroWinklerStrategy comparer = new JaroWinklerStrategy();
    Map<JsonNode, Double> res = new HashMap<>();

    for (JsonNode sound : awsmSounds) {
      res.put(sound, comparer.score(soundName, sound.get("label").asText()));
    }
    return res.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey();
  }

  public void playSound(JsonNode soundNode) {
    playTask.setAudioSourceString(soundNode.get("url").asText());
    playTask.execute();

    String soundString = soundNode.get("tags").get(0).get("value").asText() + ": " + soundNode.get("label").asText();

    this.discordClient
        .updatePresence(Presence.online(Activity.playing(soundString)))
        .subscribe();
  }
}
