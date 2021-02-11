package com.github.nsilbernagel.discordbot.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.rules.AbstractValidationRule;
import com.github.nsilbernagel.discordbot.validation.rules.Required;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Component
public class MessageTaskPreparer {

  @Autowired
  private List<AbstractValidationRule> validationRules;

  private Map<AbstractValidationRule, Class<? extends Annotation>> validationAnnotations;

  @PostConstruct
  public void getValidationAnnotations() {
    this.validationRules.stream()
        .forEach(validationRule -> {
          this.validationAnnotations.put(validationRule, validationRule.handlesAnnotation())
        });
  }

  public void execute(AbstractMessageTask messageTask) {
    List<Field> messageTaskFields = Arrays.asList(messageTask.getClass().getDeclaredFields());

    /**
     * the message tasks fields annotated as CommandParam
     */
    List<Field> commandParamFields = messageTaskFields.stream()
        .filter(field -> field.isAnnotationPresent(CommandParam.class))
        .collect(Collectors.toList());

    commandParamFields.forEach(commandParamField -> {
      Arrays.asList(commandParamField.getAnnotations())
          .stream()
          .filter(fieldValidationAnnotation -> this.validationAnnotations.containsValue(fieldValidationAnnotation))
          .forEach(fieldValidationAnnotation -> {
            this.validateField(
                messageTask.getMessageToTaskHandler().getCommandParameters().get(
                    commandParamField.getDeclaredAnnotation(CommandParam.class).value()),
                commandParamField,
                fieldValidationAnnotation);
          });

      // set the actual value of the field to that given to the command as a param
      requiredField.setAccessible(true);
      Object newFieldValue = messageTask.getMessageToTaskHandler().getCommandParameters()
          .get(requiredParamAnnotation.pos());
      try {
        requiredField.set(messageTask, newFieldValue);
      } catch (IllegalAccessException e) {
        // don't need to handle this as we set it accressible beforehand
      }

    });
  }

  private void validateField(Optional<String> command, Field commandParamField, Annotation fieldValidationAnnotation) {
    AbstractValidationRule validationRule = this.validationAnnotations.entrySet()
        .stream()
        .filter(validationAnnotation -> fieldValidationAnnotation.equals(validationAnnotation.getValue()))
        .map(Map.Entry::getKey)
        .findFirst();

    validationRule.validateAndHandle(command, fieldValidationAnnotation);
  }
}
