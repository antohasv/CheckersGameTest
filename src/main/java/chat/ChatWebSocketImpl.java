package chat;

import dbService.UserDataSet;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import frontend.UserDataImpl;

public class ChatWebSocketImpl extends WebSocketAdapter {
    public ChatWebSocketImpl() {
    }

    @Override
    public void onWebSocketText(String message) {
        if (isNotConnected()) {
            return;
        }
//		System.out.println(message);
        String sessionId = null, startServerTime = null;
        String text = null;
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(message);
            sessionId = json.get("sessionId").toString();
            startServerTime = json.get("startServerTime").toString();
            text = json.get("text").toString();
        } catch (Exception ignor) {
        }
        if ((sessionId != null) && (startServerTime != null) && (text != null) && (!text.equals("")) && (UserDataImpl.checkServerTime(startServerTime))) {
            addMessageToChat(sessionId, text);
        } else if ((sessionId != null) && (startServerTime != null) && UserDataImpl.checkServerTime(startServerTime)) {
            addNewChater(sessionId);
        }
    }

    private void addNewChater(String sessionId) {
        UserDataImpl.putSessionIdAndChatWS(sessionId, this);
    }

    private void addMessageToChat(String sessionId, String text) {
        UserDataSet user = UserDataImpl.getLogInUserBySessionId(sessionId);
        if (user != null) {
            GameChatImpl.sendMessage(sessionId, text);
        }
    }

    public static void sendMessage(String sessionId, String message) {
        try {
            UserDataImpl.getChatWSBySessionId(sessionId).sendString(message);
        } catch (Exception ignor) {
        }
    }
}