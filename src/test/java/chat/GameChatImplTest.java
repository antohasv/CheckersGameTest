package chat;

import base.GameChat;
import base.MessageSystem;
import base.UserData;
import dbService.UserDataSet;
import frontend.UserDataImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

public class GameChatImplTest {

    public static final String FAKE_SESSION_ID_1 = "fake_sessionId1";
    public static final String FAKE_SESSION_ID_2 = "fake_sessionId2";
    public static final String FAKE_TEXT = "FAKE_Text";
    public static final String FAKE_NICKNAME = "FAKE_NICKNAME";
    private GameChatImpl gameChat;

    @BeforeMethod
    public void setUp() throws Exception {
        MessageSystem messageSystem = mock(MessageSystem.class);
        gameChat = new GameChatImpl(messageSystem);
    }

    @Test
    public void testSendMessageWithoutCreateChat() throws Exception {
        //gameChat.createChat(FAKE_SESSION_ID_1, FAKE_SESSION_ID_2);
        UserDataImpl.putLogInUser(FAKE_SESSION_ID_1, new UserDataSet(100, FAKE_NICKNAME, 0, 0, 0));
        GameChatImpl.sendMessage(FAKE_SESSION_ID_1, FAKE_TEXT);
    }

    @Test
    public void testSendMessageCreateChat() throws Exception {
        UserDataImpl.putSessionIdAndChatWS(FAKE_SESSION_ID_1, null);
        gameChat.createChat(FAKE_SESSION_ID_1, FAKE_SESSION_ID_2);

        UserDataImpl.putLogInUser(FAKE_SESSION_ID_1, new UserDataSet(100, FAKE_NICKNAME, 0, 0, 0));
        GameChatImpl.sendMessage(FAKE_SESSION_ID_1, FAKE_TEXT);
    }


}
