package com.ego.model;

import java.util.Date;

public class Message {
	
	private String message;
	
	private String dateSent;
	
	private String name;
	
	public Message(String name, String message, String dateSent){
		this.name=name;
		this.message=message;
		this.dateSent=dateSent;
	}
	
	public Message(String name, String message){
		this.name=name;
		this.message=message;
	}

	public String getMessage() {
		return message;
	}

	public String getDateSent() {
		return dateSent;
	}
	
	public String getName(){
		return name;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDateSent(String dateSent) {
		this.dateSent = dateSent;
	}
	
	public void setUser(String name){
		this.name = name;
	}


	
	
	
}
