package com.github.nsilbernagel.discordbot.message.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public class InsultTask extends AbstractMessageTask implements IMessageTask{

	public final static String KEYWORD = "beleidige";
	
	public boolean canHandle(String keyword) {
	    return KEYWORD.equals(keyword);
	 }
	
	@Override
	public void execute(Message message) {  
	    HttpsURLConnection connection;
		String insult = null;
		try {
			connection = (HttpsURLConnection) new URL("https://evilinsult.com/generate_insult.php?lang=en&type=text").openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			insult= content.toString();
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.message = message;
		
		if(insult!=null) {
			this.answerMessage(insult);
		}
		
	}
}
