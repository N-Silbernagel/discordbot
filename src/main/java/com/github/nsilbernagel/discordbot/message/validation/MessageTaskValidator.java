package com.github.nsilbernagel.discordbot.message.validation;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.validation.Validator;
import org.springframework.stereotype.Component;

@Component
public class MessageTaskValidator implements Validator<MsgTaskRequest> {
  public boolean validate(MsgTaskRequest request){
    return true;
  };
}
