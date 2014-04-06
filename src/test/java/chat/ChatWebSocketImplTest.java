package chat;

import base.MessageSystem;
import dbService.UserDataSet;
import frontend.UserDataImpl;
import messageSystem.MessageSystemImpl;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class ChatWebSocketImplTest {
    public static final String TEXT = "text";
    public static final String SESSION_ID = "sessionId";
    public static final String START_SERVER_TIME = "startServerTime";
    public static final String FAKE_SESSION_ID = "Fake_sessionId";
    public static final String FAKE_MESSAGE = "Fake_Message";
    public static final String FAKE_SESSION_ID_2 = "fake_session_id_2";

    private ChatWebSocketImpl chatWebSocket;
    private GameChatImpl gameChat;

    @BeforeMethod
    public void setUp() throws Exception {
        MessageSystem messageSystem = new MessageSystemImpl();

        chatWebSocket = spy(new ChatWebSocketImpl());
        gameChat = new GameChatImpl(messageSystem);
    }

    @Test
    public void testWebSocketTextWithoutConnected() throws Exception {
        chatWebSocket.onWebSocketText(FAKE_MESSAGE);
    }

    @Test
    public void testOnWebSocketTextInitialize() throws Exception {
        when(chatWebSocket.isNotConnected()).thenReturn(false);
        String serverTime = UserDataImpl.getStartServerTime();

       chatWebSocket.onWebSocketText(getMessage(null, null));

        chatWebSocket.onWebSocketText(getMessage(FAKE_SESSION_ID, null));
        chatWebSocket.onWebSocketText(getMessage(null, serverTime));

        Session session = mock(Session.class);
        RemoteEndpoint remoteEndpoint = mock(RemoteEndpoint.class);
        when(chatWebSocket.getSession()).thenReturn(session);
        when(session.getRemote()).thenReturn(remoteEndpoint);


        chatWebSocket.onWebSocketText(getMessage(FAKE_SESSION_ID, serverTime));
        Assert.assertEquals(remoteEndpoint, UserDataImpl.getChatWSBySessionId(FAKE_SESSION_ID));
    }

    @Test
    public void testUncorrectText() throws Exception {
        when(chatWebSocket.isNotConnected()).thenReturn(false);
        String serverTime = UserDataImpl.getStartServerTime();

        chatWebSocket.onWebSocketText(getMessage(null, null));

        chatWebSocket.onWebSocketText(getMessage(FAKE_SESSION_ID, null));
        chatWebSocket.onWebSocketText(getMessage(null, serverTime));
    }

    @Test
    public void testOnWebSocketTextSendMessage() throws Exception {
        String serverTime = UserDataImpl.getStartServerTime();
        String text = "Hello World!";

        when(chatWebSocket.isNotConnected()).thenReturn(false);

        chatWebSocket.onWebSocketText(getMessage(FAKE_SESSION_ID, serverTime, text)); //user == null

        Session session = mock(Session.class);
        RemoteEndpoint remoteEndpoint = mock(RemoteEndpoint.class);
        when(chatWebSocket.getSession()).thenReturn(session);
        when(session.getRemote()).thenReturn(remoteEndpoint);

        gameChat.createChat(FAKE_SESSION_ID, FAKE_SESSION_ID_2);

        chatWebSocket.onWebSocketText(getMessage(FAKE_SESSION_ID, serverTime));
        chatWebSocket.onWebSocketText(getMessage(FAKE_SESSION_ID_2, serverTime));

        UserDataImpl.putLogInUser(FAKE_SESSION_ID, new UserDataSet());
        chatWebSocket.onWebSocketText(getMessage(FAKE_SESSION_ID, serverTime, text)); // user != null

        verify(remoteEndpoint, times(2)).sendString("{\"sender\":\"\",\"text\":\"Hello World!\"}");
        //chatWebSocket.onWebSocketText(getMessage(sessionId, serverTime, null));
    }

    @Test
    public void testSendMessage() throws Exception {
        String sessionId = FAKE_SESSION_ID;
        String serverTime = UserDataImpl.getStartServerTime();

        when(chatWebSocket.isNotConnected()).thenReturn(false);

        Session session = mock(Session.class);
        RemoteEndpoint remoteEndpoint = mock(RemoteEndpoint.class);
        when(chatWebSocket.getSession()).thenReturn(session);
        when(session.getRemote()).thenReturn(remoteEndpoint);

        chatWebSocket.onWebSocketText(getMessage(sessionId, serverTime));
        ChatWebSocketImpl.sendMessage(sessionId, FAKE_MESSAGE);
        verify(remoteEndpoint, times(1)).sendString(FAKE_MESSAGE);
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
