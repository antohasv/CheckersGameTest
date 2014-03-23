package chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import dbService.UserDataSet;
import utils.TimeHelper;

import base.Address;
import base.GameChat;
import base.MessageSystem;

import frontend.UserDataImpl;

public class GameChatImpl implements GameChat{

	private static final Map<String, List<ChatMessage>> sessionIdToChat = 
			new HashMap<String, List<ChatMessage>>();
	private static final Map<String, String> sessionIdToAnotherSessionId = 
			new HashMap<String, String>();
	private MessageSystem messageSystem;
	private Address address = new Address();

	public GameChatImpl(MessageSystem messageSystem){
		this.messageSystem = messageSystem;
		messageSystem.addService(this, "GameChat");
	}

	public Address getAddress(){
		return address;
	}

	public MessageSystem getMessageSystem(){
		return messageSystem;
	}

	public void createChat(String sessionId1, String sessionId2){
		List<ChatMessage> chat = new Vector<ChatMessage>();
		sessionIdToChat.put(sessionId1, chat);
		sessionIdToChat.put(sessionId2, chat);
		sessionIdToAnotherSessionId.put(sessionId1, sessionId2);
		sessionIdToAnotherSessionId.put(sessionId2, sessionId1);
	}

	public static void sendMessage(String sessionId, String text){
		UserDataSet sender = UserDataImpl.getLogInUserBySessionId(sessionId);
		ChatMessage message = new ChatMessage(sender.getNick(), text);
		if(sessionIdToChat.get(sessionId)!=null){
			sessionIdToChat.get(sessionId).add(message);
			ChatWebSocketImpl.sendMessage(sessionId, message.json());
			String anotherSessionId = sessionIdToAnotherSessionId.get(sessionId);
			ChatWebSocketImpl.sendMessage(anotherSessionId, message.json());
		}
	}

	public void run(){
		while(true){
			messageSystem.execForAbonent(this);
			TimeHelper.sleep(200);
		}
	}
}
