package com.github.nsilbernagel.discordbot.controllers;

import com.github.nsilbernagel.discordbot.representations.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UsersController {

  @GetMapping("/users")
  @ResponseBody
  public User view(@RequestParam(name = "id", required = true) long id) {
    return new User(id, "Nils");
  }
}
