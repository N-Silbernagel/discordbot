package com.github.nsilbernagel.discordbot.message.validation;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.validation.Validator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageTaskValidator implements Validator<MsgTaskRequest> {
  public String validate(MsgTaskRequest request){
    List<Field> fieldsAnnotatedWithCommandParam = this.commandParamAnnotatedFields(request);

    fieldsAnnotatedWithCommandParam.forEach(field -> this.setCommandParamFieldValue(field, request));

    return "";
  };

  private List<Field> commandParamAnnotatedFields(MsgTaskRequest request) {
    Field[] requestFields = request.getClass().getFields();

    return Arrays.stream(requestFields).filter(field ->
        field.getAnnotation(CommandParam.class) != null
    )
        .collect(Collectors.toList());
  }

  private void setCommandParamFieldValue(Field field, MsgTaskRequest request){
    field.setAccessible(true);
    try {
      field.set(request, request.getCommandParameters().get(field.getAnnotation(CommandParam.class).pos()));
    } catch (IllegalAccessException e) {
      // won't be illegal as we set it as accessible before
    }
  }
}
