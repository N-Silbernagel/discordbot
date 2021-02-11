package com.github.nsilbernagel.discordbot.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
  private void prepareCommandParamField(Field commandParamField) {
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

    Integer commandParamIndex = commandParamField.getAnnotation(CommandParam.class).value();

    Object newFieldValue;

    if (messageTask.getMessageToTaskHandler().getCommandParameters().size() > commandParamIndex) {
      newFieldValue = messageTask.getMessageToTaskHandler().getCommandParameters()
          .get(commandParamIndex);
    } else {
      newFieldValue = null;
    }

    try {
      commandParamField.set(messageTask, this.conversionService.convert(newFieldValue, commandParamField.getType()));
    } catch (IllegalAccessException e) {
      // don't need to handle this as we set it accressible beforehand
    }
  }

  /**
   * validate a command param field according to its validatin rule annotations
   *
   * @param commandParamField
   * @param fieldValidationAnnotation
   */
  private void validateFieldAccordingToAnnotation(Field commandParamField, Annotation fieldValidationAnnotation) {
    Optional<AValidationRule<? extends Annotation>> validationRuleOptional = this.validationRules.stream()
        .filter(validationRule -> validationRule.getCorrespondingAnnotation()
            .equals(fieldValidationAnnotation.annotationType()))
        .findFirst();

    int commandIndex = commandParamField.getAnnotation(CommandParam.class).value();
    Optional<String> commandParamValue;
    if (messageTask.getMessageToTaskHandler().getCommandParameters().size() >= (commandIndex + 1)) {
      commandParamValue = Optional.ofNullable(messageTask.getMessageToTaskHandler().getCommandParameters()
          .get(commandIndex));
    } else {
      commandParamValue = Optional.empty();
    }

    validationRuleOptional.get()
        .validate(commandParamValue, commandParamField);
  }

  @PostConstruct
  private void setUpConversionService() {
    this.conversionService = new DefaultConversionService();
  }
}
