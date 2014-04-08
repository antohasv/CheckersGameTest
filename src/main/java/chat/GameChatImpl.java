package chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.inject.Inject;
import dbService.UserDataSet;
import utils.TimeHelper;

import base.Address;
import base.GameChat;
import base.MessageSystem;

import frontend.UserDataImpl;

public class GameChatImpl implements GameChat {

    public static final String SERVICE_NAME = "GameChat";
    public static final int TICK_TIME = 200;

    private static final Map<String, List<ChatMessage>> sessionIdToChat =
            new HashMap<String, List<ChatMessage>>();

    private static final Map<String, String> sessionIdToAnotherSessionId =
            new HashMap<String, String>();

    private MessageSystem messageSystem;
    private Address address = new Address();

    @Inject
    public GameChatImpl(MessageSystem messageSystem) {
        this.messageSystem = messageSystem;
        messageSystem.addService(this, SERVICE_NAME);
    }

    public Address getAddress() {
        return address;
    }

    public MessageSystem getMessageSystem() {
        return messageSystem;
    }

    public void createChat(String sessionId1, String sessionId2) {
        List<ChatMessage> chat = new Vector<ChatMessage>();
        sessionIdToChat.put(sessionId1, chat);
        sessionIdToChat.put(sessionId2, chat);
        sessionIdToAnotherSessionId.put(sessionId1, sessionId2);
        sessionIdToAnotherSessionId.put(sessionId2, sessionId1);
    }

    public static void sendMessage(String sessionId, String text) {
        UserDataSet sender = UserDataImpl.getLogInUserBySessionId(sessionId);
        ChatMessage message = new ChatMessage(sender.getNickName(), text);
        if (sessionIdToChat.get(sessionId) != null) {
            sessionIdToChat.get(sessionId).add(message);
            ChatWebSocketImpl.sendMessage(sessionId, message.getJson());

            String anotherSessionId = sessionIdToAnotherSessionId.get(sessionId);
            ChatWebSocketImpl.sendMessage(anotherSessionId, message.getJson());
        }
    }

    public void run() {
        while (true) {
            messageSystem.execForAbonent(this);
            TimeHelper.sleep(TICK_TIME);
        }
    }
}
