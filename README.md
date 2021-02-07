# Discordbot

### A discord bot built with
- java
- spring
- [Discord4J](https://github.com/Discord4J/Discord4J)

### Requirements
- JDK 14

### Set Up
1. Set up a discord bot https://discord.com/developers/applications
2. get it's token from the oauth2 tab
3. duplicate the .application-local.properties.example file as application-local.properties. this is where our local config goes
4. paste your bot token in the app.discord.token property
5. Build using the gradle wrapper

### Adding your own tasks
Create a class under com.github.nsilbernagel.discordbot.message.impl that implements AbstractMessageTask. These tasks will be auto added to the available bot commands