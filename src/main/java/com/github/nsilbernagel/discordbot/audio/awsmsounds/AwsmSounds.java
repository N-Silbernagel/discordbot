package com.github.nsilbernagel.discordbot.audio.awsmsounds;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import com.github.nsilbernagel.discordbot.audio.SoundsSource;
import com.github.nsilbernagel.discordbot.audio.awsmsounds.dto.AwsmSound;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import net.ricecode.similarity.JaroWinklerStrategy;
import reactor.core.publisher.Flux;

@Component
public class AwsmSounds extends SoundsSource<AwsmSound> {
  public static final double MIN_MATCH_SCORE = 0.75;

  private JaroWinklerStrategy comparer;

  private final Flux<AwsmSound> cache;

  public AwsmSounds() {
    List<AwsmSound> soundsList = WebClient.create("https://sounds-backend.awsm.rocks")
        .get()
        .uri("/api/sounds")
        .accept(MediaType.APPLICATION_JSON)
        .header("token",
            "ysOp8JjBAbFhMoDEWWRbMHBAYqEJOAopkFRQvHSogTIosB500tV3ZvjMaH8l1wUTosU3LwtQEzR8xZ7lcwwHsk0ymFtPgXHbDzQUuOFtRlVgnrZ9FqFgb9mq5x7Ifqe6")
        .retrieve()
        .toEntityList(AwsmSound.class)
        .block()
        .getBody();

    this.cache = Flux.fromIterable(soundsList).cache();
  }

  public Flux<AwsmSound> fetch() {
    return this.cache;
  }

  public Optional<AwsmSound> filter(String query) {
    AtomicReference<AwsmSound> matchingSound = new AtomicReference<>();
    AtomicReference<Double> matchScore = new AtomicReference<>(MIN_MATCH_SCORE);
    this.fetch().doOnEach((sound) -> {
      if (sound.get() == null) {
        return;
      }
      double currentMatchScore = this.comparer.score(query, sound.get().getLabel());
      if (currentMatchScore < matchScore.get()) {
        return;
      }

      matchScore.set(currentMatchScore);
      matchingSound.set(sound.get());
    }).blockLast();

    return Optional.ofNullable(matchingSound.get());
  }

  public AwsmSound random() {
    List<AwsmSound> allSounds = this.fetch().collectList().block();
    return allSounds.get((new Random()).nextInt(allSounds.size()));
  }

  @PostConstruct
  public void setUpComparer() {
    this.comparer = new JaroWinklerStrategy();
  }
}
