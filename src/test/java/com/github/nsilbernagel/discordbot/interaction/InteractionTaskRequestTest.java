package com.github.nsilbernagel.discordbot.interaction;

import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.core.object.command.Interaction;
import discord4j.discordjson.json.ApplicationCommandInteractionData;
import discord4j.discordjson.json.ApplicationCommandInteractionOptionData;
import discord4j.discordjson.json.InteractionData;
import discord4j.discordjson.possible.Possible;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteractionTaskRequestTest {
  @Mock
  private InteractionCreateEvent interactionCreateEvent;
  @Mock
  private Interaction interaction;
  @Mock
  private InteractionData interactionData;
  @Mock
  private ApplicationCommandInteractionData applicationCommandInteractionData;

  private final String commandName = "test";

  @BeforeEach
  public void setUp() {
    when(interactionCreateEvent.getCommandName()).thenReturn(commandName);

    when(interactionCreateEvent.getInteraction()).thenReturn(interaction);
    when(interaction.getData()).thenReturn(interactionData);
  }

  @Test
  public void its_command_name_equals_the_event_command_name_when_the_event_has_no_data() {
    when(interactionData.data()).thenReturn(Possible.absent());

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(interactionTaskRequest.getCommandName(), commandName);
  }

  @Test
  public void its_command_name_equals_the_event_command_name_when_no_options_are_available() {
    when(interactionData.data()).thenReturn(Possible.of(applicationCommandInteractionData));

    when(applicationCommandInteractionData.options()).thenReturn(Possible.absent());

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(interactionTaskRequest.getCommandName(), commandName);
  }

  @Test
  public void its_command_name_equals_the_event_command_name_when_no_subcommands_are_available() {
    when(interactionData.data()).thenReturn(Possible.of(applicationCommandInteractionData));

    List<ApplicationCommandInteractionOptionData> interactionOptionDataList = new ArrayList<>(0);

    when(applicationCommandInteractionData.options()).thenReturn(Possible.of(interactionOptionDataList));

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(interactionTaskRequest.getCommandName(), commandName);
  }

  @Test
  public void its_command_name_is_constructed_of_the_slash_commands_name_and_the_chosen_subcommands() {
    when(interactionData.data()).thenReturn(Possible.of(applicationCommandInteractionData));

    String subcommandName = "sub";

    ApplicationCommandInteractionOptionData subCommand = mock(ApplicationCommandInteractionOptionData.class);
    List<ApplicationCommandInteractionOptionData> slashCommandOptions = List.of(subCommand);

    ApplicationCommandInteractionOptionData subCommandOption = mock(ApplicationCommandInteractionOptionData.class);
    List<ApplicationCommandInteractionOptionData> subcommandOptions = List.of(subCommandOption);

    when(subCommand.options()).thenReturn(Possible.of(subcommandOptions));

    when(applicationCommandInteractionData.options()).thenReturn(Possible.of(slashCommandOptions));

    when(subCommand.name()).thenReturn(subcommandName);
    when(subCommandOption.options()).thenReturn(Possible.absent());

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(commandName + "/" + subcommandName, interactionTaskRequest.getCommandName());
  }
}