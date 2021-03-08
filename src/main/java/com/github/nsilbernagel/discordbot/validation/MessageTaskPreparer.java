package com.github.nsilbernagel.discordbot.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.validation.rules.AValidationRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

@Component
public class MessageTaskPreparer {

  @Autowired
  private List<AValidationRule<? extends Annotation>> validationRules;

  private AbstractMessageTask messageTask;

  private ConversionService conversionService;

  /**
   * Prepare a message task, validate and map the command parameters to the
   * corresponding fields in the message task
   *
   * @param messageTask
   */
  public void execute(AbstractMessageTask messageTask) {
    this.messageTask = messageTask;

    List<Field> messageTaskFields = Arrays.asList(messageTask.getClass().getDeclaredFields());

    /**
     * the message tasks fields annotated as CommandParam
     */
    List<Field> commandParamFields = messageTaskFields.stream()
        .filter(field -> field.isAnnotationPresent(CommandParam.class))
        .collect(Collectors.toList());

    commandParamFields.forEach(commandParamField -> {
      this.prepareCommandParamField(commandParamField);
    });
  }

  /**
   * validate and map one command param fields
   *
   * @param commandParamField
   */
  private void prepareCommandParamField(Field commandParamField) throws IllegalArgumentException {
    Arrays.stream(commandParamField.getAnnotations())
        .filter(fieldValidationAnnotation -> this.validationRules.stream()
            .filter(validationRule -> validationRule.getCorrespondingAnnotation().equals(
                fieldValidationAnnotation.annotationType()))
            .findFirst().isPresent())
        .forEach(fieldValidationAnnotation -> {
          this.validateFieldAccordingToAnnotation(commandParamField, fieldValidationAnnotation);
        });

    // set the actual value of the field to that given to the command as a param
    commandParamField.setAccessible(true);

    Integer commandParamIndex = commandParamField.getAnnotation(CommandParam.class).pos();

    // a command param can go over many, space seperated, params, which means it is
    // going to be a list
    int commandRange = this.getCommandParamRange(commandParamField.getAnnotation(CommandParam.class));

    if (commandRange == 1) {
      this.setFieldValue(messageTask, commandParamField, this.getFieldValueFromCommandParam(commandParamIndex));
    } else {
      List<Object> newFieldValue = new ArrayList<Object>();
      for (int offset = 0; offset < commandRange; offset++) {
        newFieldValue.add(this.getFieldValueFromCommandParam(commandParamIndex + offset));
      }
      this.setFieldValue(messageTask, commandParamField, newFieldValue);
    }
  }

  private void setFieldValue(AbstractMessageTask messageTask, Field commandParamField, Object value) {
    try {
      commandParamField.set(
          messageTask,
          this.conversionService.convert(
              value,
              commandParamField.getType()));
    } catch (IllegalAccessException e) {
      // don't need to handle this as we set it accressible beforehand
    }
  }

  private Object getFieldValueFromCommandParam(int commandParamIndex) {
    if (messageTask.getMessageToTaskHandler().getCommandParameters().size() > commandParamIndex) {
      return messageTask.getMessageToTaskHandler()
          .getCommandParameters()
          .get(commandParamIndex);
    } else {
      return null;
    }
  }

  /**
   * validate a command param field according to its validatin rule annotations
   *
   * @param commandParamField
   * @param fieldValidationAnnotation
   */
  private void validateFieldAccordingToAnnotation(Field commandParamField, Annotation fieldValidationAnnotation)
      throws MessageValidationException, IllegalArgumentException {
    Optional<AValidationRule<? extends Annotation>> validationRuleOptional = this.validationRules.stream()
        .filter(validationRule -> validationRule.getCorrespondingAnnotation()
            .equals(fieldValidationAnnotation.annotationType()))
        .findFirst();

    int commandIndex = commandParamField.getAnnotation(CommandParam.class).pos();
    // a command param can go over many, space seperated, params, which means it is
    // going to be a list
    int commandRange = this.getCommandParamRange(commandParamField.getAnnotation(CommandParam.class));

    for (int offset = 0; offset < commandRange; offset++) {
      Optional<String> commandParamValue;
      if (messageTask.getMessageToTaskHandler().getCommandParameters().size() >= (commandIndex + offset + 1)) {
        commandParamValue = Optional.ofNullable(
            messageTask.getMessageToTaskHandler()
                .getCommandParameters()
                .get(commandIndex + offset));
      } else {
        commandParamValue = Optional.empty();
      }

      validationRuleOptional.get()
          .validate(commandParamValue, commandParamField);
    }
  }

  private int getCommandParamRange(CommandParam commandParamAnnotation) throws IllegalArgumentException {
    int range = commandParamAnnotation.range();
    if (range < 1) {
      throw new IllegalArgumentException("Command Param range cannot be smaller than 1.");
    }
    return range;
  }

  @PostConstruct
  private void setUpConversionService() {
    this.conversionService = new DefaultConversionService();
  }
}
