package chat;

import dbService.UserDataSet;
import frontend.FrontendImpl;
import frontend.UserDataImpl;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ChatWebSocketImpl extends WebSocketAdapter {

    private Chater chater;
    public ChatWebSocketImpl() {
        chater = new Chater(this);
    }

    @Override
    public void onWebSocketText(String message) {
        if (isNotConnected()) {
            return;
        }
        chater.webSocketText(ReceiveMessage.getMessage(message));
    }

    public static void sendMessage(String sessionId, String message) {
        Chater.sendMessage(sessionId, message);
    }
}