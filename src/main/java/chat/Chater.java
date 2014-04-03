package chat;

import dbService.UserDataSet;
import frontend.UserDataImpl;

public class Chater {

    private ChatWebSocketImpl chatWebSocket;

    public Chater(ChatWebSocketImpl chatWebSocket) {
        this.chatWebSocket = chatWebSocket;
    }

    public void webSocketText(ReceiveMessage message) {
        String sessionId = message.getSessionId();
        String startServerTime = message.getStartServerTime();
        String text = message.getText();

        if (sessionId != null && startServerTime != null && text != null && !text.equals("") && UserDataImpl.checkServerTime(startServerTime)) {
            addMessageToChat(sessionId, text);
        } else if (sessionId != null && startServerTime != null && UserDataImpl.checkServerTime(startServerTime)) {
            addNewChater(sessionId);
        }
    }

    private void addNewChater(String sessionId) {
        UserDataImpl.putSessionIdAndChatWS(sessionId, chatWebSocket);
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
