package chat;

import dbService.UserDataSet;
import frontend.FrontendImpl;
import frontend.UserDataImpl;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utils.TextUtils;

public class ChatWebSocketImpl extends WebSocketAdapter {

    public ChatWebSocketImpl() {
    }

    @Override
    public void onWebSocketText(String msgText) {
        if (isNotConnected()) {
            return;
        }

        ReceiveMessage message = ReceiveMessage.getMessage(msgText);
        String sessionId = message.getSessionId();
        String startServerTime = message.getStartServerTime();
        String text = message.getText();

        if (sessionId != null && startServerTime != null && UserDataImpl.checkServerTime(startServerTime)) {
            if (TextUtils.isEmpty(text)) {
                addNewChater(sessionId);
            } else {
                addMessageToChat(sessionId, text);
            }
        }
    }

    public static void sendMessage(String sessionId, String message) {
        try {
            UserDataImpl.getChatWSBySessionId(sessionId).sendString(message);
        } catch (Exception e) {
            e.printStackTrace();
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
}