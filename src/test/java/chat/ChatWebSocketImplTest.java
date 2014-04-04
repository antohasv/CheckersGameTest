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

    //{"sessionId":"6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b","startServerTime":"1c8b5773a39cddd084cb33d37cab6cd5c540e52b770e2d269039a619d0dfabf5"}
    //{"sessionId":"d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35","startServerTime":"1c8b5773a39cddd084cb33d37cab6cd5c540e52b770e2d269039a619d0dfabf5"}
    //{"text":"dsadsadas","sessionId":"6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b","startServerTime":"1c8b5773a39cddd084cb33d37cab6cd5c540e52b770e2d269039a619d0dfabf5"}

    //{"text":"fdsfdsf","sessionId":"d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35","startServerTime":"1c8b5773a39cddd084cb33d37cab6cd5c540e52b770e2d269039a619d0dfabf5"}
    //{"text":"fdsfdsf","sessionId":"d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35","startServerTime":"1c8b5773a39cddd084cb33d37cab6cd5c540e52b770e2d269039a619d0dfabf5"}

    @Test
    public void testonWebSocketTextInitialize() throws Exception {
        String sessionId = FAKE_SESSION_ID;
        String serverTime = UserDataImpl.getStartServerTime();

        ChatWebSocketImpl chatWebSocket = spy(new ChatWebSocketImpl());
        when(chatWebSocket.isNotConnected()).thenReturn(false);

        chatWebSocket.onWebSocketText(getMessage(null, null));
        chatWebSocket.onWebSocketText(getMessage(sessionId, serverTime));
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
