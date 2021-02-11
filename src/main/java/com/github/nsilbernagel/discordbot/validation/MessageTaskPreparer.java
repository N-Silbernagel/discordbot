package com.github.nsilbernagel.discordbot.validation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.validation.rules.EValidationRule;

import org.springframework.stereotype.Component;

@Component
public class MessageTaskPreparer {

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
          .filter(fieldValidationAnnotation -> Arrays.asList(EValidationRule.values()).stream()
              .filter(validationRule -> validationRule.getCorrespondingAnnotation().equals(
                  fieldValidationAnnotation.getClass()))
              .findFirst().isPresent())
          .forEach(fieldValidationAnnotation -> {
            Optional<EValidationRule> validationRuleOptional = Arrays.stream(EValidationRule.values())
                .filter(validationRule -> validationRule.getCorrespondingAnnotation()
                    .equals(fieldValidationAnnotation.getClass()))
                .findFirst();

            if (!validationRuleOptional.get()
                .validate(Optional.ofNullable(messageTask.getMessageToTaskHandler().getCommandParameters()
                    .get(commandParamField.getAnnotation(CommandParam.class).value())), commandParamField)) {
              validationRuleOptional.get().handleInvalid();
            }

            // set the actual value of the field to that given to the command as a param
            commandParamField.setAccessible(true);
            Object newFieldValue = messageTask.getMessageToTaskHandler().getCommandParameters()
                .get(commandParamField.getAnnotation(CommandParam.class).value());
            try {
              commandParamField.set(messageTask, newFieldValue);
            } catch (IllegalAccessException e) {
              // don't need to handle this as we set it accressible beforehand
            }
          });
    });
  }
}
