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
  public final static List<Integer> SUBCOMMAND_OR_GROUP = List.of(1, 2);
  @Getter
  @NonNull
  private final InteractionCreateEvent event;

  @Getter
  @NonNull
  private final String commandName;

  @Getter
  private final List<ApplicationCommandInteractionOptionData> options;

  public static InteractionTaskRequest fromEvent(InteractionCreateEvent event) {
    StringBuilder commandName = new StringBuilder(event.getCommandName());

    Optional<ApplicationCommandInteractionData> data = event.getInteraction()
        .getData()
        .data()
        .toOptional();

    // if the command has no data, only use the slash command's name
    if (data.isEmpty() || data.get().options().isAbsent()){
      return new InteractionTaskRequest(event, commandName.toString(), new ArrayList<>(0));
    }

    List<ApplicationCommandInteractionOptionData> requestOptions = data.get().options().get();

    Optional<ApplicationCommandInteractionOptionData> optionData = requestOptions.stream()
        .filter(InteractionTaskRequest::isSubCommandOrGroup)
        .findFirst();

    // while there are options that have options (thus being sub commands), append their names to the command name
    while (optionData.isPresent()){
      commandName.append("/").append(optionData.get().name());

      if(optionData.get().options().isAbsent()){
        return new InteractionTaskRequest(event, commandName.toString(), new ArrayList<>(0));
      }

      requestOptions = optionData.get().options().get();

      optionData = optionData.get().options().get().stream()
          .filter(InteractionTaskRequest::isSubCommandOrGroup)
          .findFirst();
    }

    return new InteractionTaskRequest(event, commandName.toString(), requestOptions);
  }

  public CommandParam getOptionValue(String optionName) {
    if(this.getOptions() == null) {
      return CommandParam.empty();
    }
    Optional<ApplicationCommandInteractionOptionData> optionData = this.getOptions().stream()
        .filter(option -> option.name().equals(optionName))
        .findAny();

    if (optionData.isEmpty() || optionData.get().value().isAbsent()){
      return CommandParam.empty();
    }

    return new CommandParam(optionData.get().value().get());
  }

  private static boolean isSubCommandOrGroup(ApplicationCommandInteractionOptionData option) {
    return SUBCOMMAND_OR_GROUP.contains(option.type());
  }
}
