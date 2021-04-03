package com.github.nsilbernagel.discordbot.help;

import java.util.List;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.TaskException;

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

  @Override
  public void action(MsgTaskRequest taskRequest) {
    String taskToExplainQuery = taskRequest.param(0)
        .as(String.class);

    // explain all task if none is given
    if (taskToExplainQuery == null) {
      taskRequest.respond(this.generateHelpMarkdown()).block();
      return;
    }

    Optional<ExplainedMessageTask> taskToExplain = explainedMessageTasks.stream()
        .filter(explainedMessageTask -> explainedMessageTask.getKeyword().equals(taskToExplainQuery))
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
