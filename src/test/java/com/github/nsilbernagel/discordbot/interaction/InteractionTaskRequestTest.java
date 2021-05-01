package com.github.nsilbernagel.discordbot.interaction;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
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
  private ApplicationCommandInteractionData commandData;

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
    when(interactionData.data()).thenReturn(Possible.of(commandData));

    when(commandData.options()).thenReturn(Possible.absent());

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(interactionTaskRequest.getCommandName(), commandName);
  }

  @Test
  public void its_command_name_equals_the_event_command_name_when_no_subcommands_are_available() {
    when(interactionData.data()).thenReturn(Possible.of(commandData));

    List<ApplicationCommandInteractionOptionData> interactionOptionDataList = new ArrayList<>(0);

    when(commandData.options()).thenReturn(Possible.of(interactionOptionDataList));

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(interactionTaskRequest.getCommandName(), commandName);
  }

  @Test
  public void its_command_name_is_constructed_of_the_slash_commands_name_and_the_chosen_subcommands() {
    when(interactionData.data()).thenReturn(Possible.of(commandData));

    String subcommandName = "sub";

    ApplicationCommandInteractionOptionData subCommand = mock(ApplicationCommandInteractionOptionData.class);
    List<ApplicationCommandInteractionOptionData> slashCommandOptions = List.of(subCommand);
    List<ApplicationCommandInteractionOptionData> subCommandOptions = new ArrayList<>(0);

    when(subCommand.type()).thenReturn(1);

    when(commandData.options()).thenReturn(Possible.of(slashCommandOptions));

    when(subCommand.name()).thenReturn(subcommandName);
    when(subCommand.options()).thenReturn(Possible.of(subCommandOptions));

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(commandName + "/" + subcommandName, interactionTaskRequest.getCommandName());
  }

  @Test
  public void it_returns_an_empty_command_param_for_option_values_when_there_are_no_options() {
    when(interactionData.data()).thenReturn(Possible.of(commandData));

    when(commandData.options()).thenReturn(Possible.absent());

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(interactionTaskRequest.getOptionValue("test"), CommandParam.empty());
  }

  @Test
  public void it_returns_an_empty_command_param_for_option_values_when_the_option_is_not_available() {
    when(interactionData.data()).thenReturn(Possible.of(commandData));

    ApplicationCommandInteractionOptionData otherOption = mock(ApplicationCommandInteractionOptionData.class);
    when(otherOption.name()).thenReturn("someothername");
    List<ApplicationCommandInteractionOptionData> options = List.of(otherOption);
    when(commandData.options()).thenReturn(Possible.of(options));

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(interactionTaskRequest.getOptionValue("test"), CommandParam.empty());
  }

  @Test
  public void it_returns_a_command_param_with_the_value_of_an_option() {
    String optionName = "test";
    String optionValue = "value";

    when(interactionData.data()).thenReturn(Possible.of(commandData));

    ApplicationCommandInteractionOptionData option = mock(ApplicationCommandInteractionOptionData.class);
    when(option.name()).thenReturn(optionName);
    when(option.value()).thenReturn(Possible.of(optionValue));
    List<ApplicationCommandInteractionOptionData> options = List.of(option);
    when(commandData.options()).thenReturn(Possible.of(options));

    InteractionTaskRequest interactionTaskRequest = InteractionTaskRequest.fromEvent(interactionCreateEvent);

    assertEquals(interactionTaskRequest.getOptionValue(optionName).getRaw(), optionValue);
  }
}