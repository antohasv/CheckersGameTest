package chat;

import base.Frontend;
import dbService.UserDataSet;
import frontend.FrontendImpl;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import frontend.UserDataImpl;

public class ChatWebSocketImpl extends WebSocketAdapter {

    public static final String TEXT = "text";

    public ChatWebSocketImpl() {
    }

    @Override
    public void onWebSocketText(String message) {
        if (isNotConnected()) {
            return;
        }

        String sessionId = null, startServerTime = null;
        String text = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(message);

            sessionId = json.get(FrontendImpl.SESSION_ID).toString();
            startServerTime = json.get(FrontendImpl.SERVER_TIME).toString();
            if (json.get(TEXT) != null) {
                text = json.get(TEXT).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sessionId != null && startServerTime != null && text != null && !text.equals("") && UserDataImpl.checkServerTime(startServerTime)) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}