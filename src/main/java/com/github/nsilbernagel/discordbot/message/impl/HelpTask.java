package com.github.nsilbernagel.discordbot.message.impl;

import java.util.List;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.CommandParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpTask extends AbstractMessageTask {
  public final static String KEYWORD = "help";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Autowired
  private List<ExplainedMessageTask> explainedMessageTasks;

  @CommandParam(pos = 0)
  private String taskToExplainString;

  @Override
  public void action() {

    if (this.taskToExplainString == null) {
      this.answerMessage(this.generateHelpMarkdown()).block();
      return;
    }

    Optional<ExplainedMessageTask> taskToExplain = explainedMessageTasks.stream()
        .filter(explainedMessageTask -> explainedMessageTask.getKeyword().equals(taskToExplainString))
        .findFirst();

    if (!taskToExplain.isPresent()) {
      throw new TaskException("Den Befehl gibt es nicht.");
    }

    this.answerMessage(taskToExplain.get().getExplaination()).block();
  }

  private String generateHelpMarkdown() {
    String helpMarkdownString = "```\n";
    for (ExplainedMessageTask explainedMessageTask : explainedMessageTasks) {
      helpMarkdownString += explainedMessageTask.getKeyword() + ": " + explainedMessageTask.getExplaination() + "\n";
    }
    helpMarkdownString += "```";
    return helpMarkdownString;
  }
}
