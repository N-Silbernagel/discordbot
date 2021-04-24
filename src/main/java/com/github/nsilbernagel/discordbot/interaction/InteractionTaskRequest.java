package com.github.nsilbernagel.discordbot.interaction;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.task.TaskRequest;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.discordjson.json.ApplicationCommandInteractionData;
import discord4j.discordjson.json.ApplicationCommandInteractionOptionData;
import lombok.*;
import org.springframework.lang.NonNull;

import java.util.*;

// TODO: add method for getting resolved values as commandParam
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class InteractionTaskRequest extends TaskRequest {
  @Getter
  @NonNull
  private final InteractionCreateEvent event;

  @Getter
  @NonNull
  private final String commandName;

  @Getter
  private final List<ApplicationCommandInteractionOptionData> options;

  public static InteractionTaskRequest fromEvent(InteractionCreateEvent event) {
    //get command name for the request, building it once
    //Get the command name by using the name of the original slash command plus all the subcommands, separated by slash

    StringBuilder commandName = new StringBuilder(event.getCommandName());

    Optional<ApplicationCommandInteractionData> data = event.getInteraction()
        .getData()
        .data().toOptional();

    // if the command has no data, only use the slash command's name
    if (data.isEmpty() || data.get().options().toOptional().isEmpty()){
      return new InteractionTaskRequest(event, commandName.toString(), null);
    }

    List<ApplicationCommandInteractionOptionData> requestOptions = data.get().options().get();

    Optional<ApplicationCommandInteractionOptionData> optionData = requestOptions.stream()
        .filter(option -> !option.options().isAbsent())
        .findFirst();

    // while there are options that have options (thus being sub commands), append their names to the command name
    while (optionData.isPresent()){
      requestOptions = optionData.get().options().get();

      commandName.append("/").append(optionData.get().name());

      optionData = optionData.get().options().get().stream()
          .filter(option -> !option.options().isAbsent())
          .findFirst();
    }

    return new InteractionTaskRequest(event, commandName.toString(), requestOptions);
  }

  public CommandParam getOptionValue(String optionName) {
    Optional<ApplicationCommandInteractionOptionData> optionData = this.getOptions().stream()
        .filter(option -> option.name().equals(optionName))
        .findAny();

    if (optionData.isEmpty() || optionData.get().value().isAbsent()){
      return CommandParam.empty();
    }

    return new CommandParam(optionData.get().value().get());
  }
}
