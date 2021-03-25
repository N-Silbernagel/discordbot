package com.github.nsilbernagel.discordbot.help;

import java.util.List;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.CommandParam;

import com.github.nsilbernagel.discordbot.validation.rules.annotations.Required;
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
  public void action() {

    // explain all task if none is given
    if (this.taskToExplainQuery.isEmpty()) {
      this.answerMessage(this.generateHelpMarkdown()).block();
      return;
    }

    Optional<ExplainedMessageTask> taskToExplain = explainedMessageTasks.stream()
        .filter(explainedMessageTask -> explainedMessageTask.getKeyword().equals(this.taskToExplainQuery.get()))
        .findFirst();

    if (taskToExplain.isEmpty()) {
      throw new TaskException("Den Befehl gibt es nicht.");
    }

    this.answerMessage(taskToExplain.get().getExplaination()).block();
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