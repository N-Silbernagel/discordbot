package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.task.TaskRequest;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@ToString
public class MsgTaskRequest extends TaskRequest {
  /**
   * The Message associated with the task, e.g. the message containing the command for a MessageTask
   */
  @Getter
  private final Message message;

  /**
   * The TextChannel associated with the task, e.g. the TextChannel the message containing the command was written on for a MessageTask
   */
  @Getter
  private final TextChannel channel;

  /**
   * The Member associated with the task, e.g. the Member who wrote the message containing the command for a MessageTask
   */
  @Getter
  private final Member author;

  /**
   * The token used to indicate a command in a message
   */
  @Getter
  private final String commandToken;

  /**
   * The string after the commandToken in the message's content, e.g. !COMMAND
   */
  private String command = null;

  private List<String> commandParameters = null;

  /**
   * Send message on the same message channel as the event was received on
   */
  public Mono<Message> respond(String responseText) {
    return this.getChannel().createMessage(responseText);
  }

  public List<String> getCommandParameters() {
    if(this.commandParameters == null) {
      this.commandParameters = Arrays.stream(this.getMessage().getContent()
          .split(" "))
          // skip first element as it is the command, not a param
          .skip(1)
          .collect(Collectors.toList());
    }
    return this.commandParameters;
  }

  public String getCommand() {
    if(this.command == null) {
      this.command = message.getContent().toLowerCase()
          .split(" ")[0]
          .replaceFirst(this.commandToken, "");
    }
    return this.command;
  }

  public CommandParam param(int index){
    String rawValue;

    if(this.getCommandParameters().size() > index){
      rawValue = this.getCommandParameters().get(index);
    } else {
      rawValue = null;
    }

    return new CommandParam(rawValue);
  }

  public CommandParam param(int first, int last){
    int lastIndex = last;
    String rawValue;

    if (last > this.getCommandParameters().size()){
      lastIndex = this.getCommandParameters().size();
    }

    if(this.getCommandParameters().size() > first){
      rawValue = String.join(
          " ",
          this.getCommandParameters().subList(first, lastIndex)
      );
    } else {
      rawValue = null;
    }

    return new CommandParam(rawValue);
  }
}
