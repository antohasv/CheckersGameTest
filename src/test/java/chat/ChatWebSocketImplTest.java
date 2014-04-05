package chat;

import dbService.UserDataSet;
import frontend.UserDataImpl;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ChatWebSocketImplTest {
    public static final String TEXT = "text";
    public static final String SESSION_ID = "sessionId";
    public static final String START_SERVER_TIME = "startServerTime";
    public static final String FAKE_SESSION_ID = "Fake_sessionId";
    public static final String FAKE_MESSAGE = "Fake_Message";

    @Test
    public void testWebSocketTextWithoutConnected() throws Exception {
        ChatWebSocketImpl chatWebSocket = new ChatWebSocketImpl();
        chatWebSocket.onWebSocketText(FAKE_MESSAGE);
    }


    @Test
    public void testonWebSocketTextInitialize() throws Exception {
        ChatWebSocketImpl chatWebSocket = spy(new ChatWebSocketImpl());
        when(chatWebSocket.isNotConnected()).thenReturn(false);

        String serverTime = UserDataImpl.getStartServerTime();

        chatWebSocket.onWebSocketText(getMessage(null, null));

        chatWebSocket.onWebSocketText(getMessage(FAKE_SESSION_ID, null));
        chatWebSocket.onWebSocketText(getMessage(null, serverTime));

        chatWebSocket.onWebSocketText(getMessage(FAKE_SESSION_ID, serverTime));
    }

    @Test
    public void testonWebSocketTextSendMessage() throws Exception {
        String sessionId = FAKE_SESSION_ID;
        String serverTime = UserDataImpl.getStartServerTime();
        String text = "Hello World!";

        ChatWebSocketImpl chatWebSocket = spy(new ChatWebSocketImpl());
        when(chatWebSocket.isNotConnected()).thenReturn(false);

        chatWebSocket.onWebSocketText(getMessage(sessionId, serverTime, text));

        UserDataImpl.putLogInUser(sessionId, new UserDataSet());
        chatWebSocket.onWebSocketText(getMessage(sessionId, serverTime, text));
        chatWebSocket.onWebSocketText(getMessage(sessionId, serverTime, null));
    }

    @Test
    public void testSendMessage() throws Exception {
        String sessionId = FAKE_SESSION_ID;
        String serverTime = UserDataImpl.getStartServerTime();

        ChatWebSocketImpl chatWebSocket = spy(new ChatWebSocketImpl());
        when(chatWebSocket.isNotConnected()).thenReturn(false);

        chatWebSocket.onWebSocketText(getMessage(sessionId, serverTime));
        ChatWebSocketImpl.sendMessage(sessionId, FAKE_MESSAGE);
    }

    public String getMessage(String sessionId, String serverTime) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SESSION_ID, sessionId);
        jsonObject.put(START_SERVER_TIME, serverTime);
        return jsonObject.toJSONString();
    }

    public String getMessage(String sessionId, String serverTime, String text) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SESSION_ID, sessionId);
        jsonObject.put(START_SERVER_TIME, serverTime);
        jsonObject.put(TEXT, text);
        return jsonObject.toJSONString();
    }
}
