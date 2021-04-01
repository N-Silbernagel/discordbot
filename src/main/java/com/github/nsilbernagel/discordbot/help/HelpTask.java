package com.github.nsilbernagel.discordbot.help;

import java.util.List;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.TaskException;
import com.github.nsilbernagel.discordbot.validation.CommandParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpTask extends MessageTask {
  public final static String KEYWORD = "help";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Autowired
  private List<ExplainedMessageTask> explainedMessageTasks;

  @CommandParam(pos = 0)
  private Optional<String> taskToExplainQuery;

  @Override
  public void action(MsgTaskRequest taskRequest) {

    // explain all task if none is given
    if (this.taskToExplainQuery.isEmpty()) {
      taskRequest.respond(this.generateHelpMarkdown()).block();
      return;
    }

    Optional<ExplainedMessageTask> taskToExplain = explainedMessageTasks.stream()
        .filter(explainedMessageTask -> explainedMessageTask.getKeyword().equals(this.taskToExplainQuery.get()))
        .findFirst();

    if (taskToExplain.isEmpty()) {
      throw new TaskException("Den Befehl gibt es nicht.");
    }

    taskRequest.respond(taskToExplain.get().getExplaination()).block();
  }

  private String generateHelpMarkdown() {
    StringBuilder helpMarkdownString = new StringBuilder("```\n");
    for (ExplainedMessageTask explainedMessageTask : explainedMessageTasks) {
      helpMarkdownString.append(explainedMessageTask.getKeyword()).append(": ").append(explainedMessageTask.getExplaination()).append("\n");
    }
    helpMarkdownString.append("```");
    return helpMarkdownString.toString();
  }
}
