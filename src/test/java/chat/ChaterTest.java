package chat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

public class ChaterTest {

    public static final String FAKE_SESSION = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b";
    public static final String FAKE_SERVER_TIME = "653e45484ae81d4c65b29a8e66231fbab74c7a91e8faf4209b7f6be9196cc944";
    public static final String FAKE_TEXT = "FAKE_TEXT";
    private Chater chater;

    @BeforeMethod
    public void setUp() throws Exception {
        ChatWebSocketImpl chatWebSocket = mock(ChatWebSocketImpl.class);
        chater = new Chater(chatWebSocket);
    }
    //{"text":"fdsfsdfsdf","sessionId":"d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35","startServerTime":"653e45484ae81d4c65b29a8e66231fbab74c7a91e8faf4209b7f6be9196cc944"}
     //{"text":"fsdsfdsf","sessionId":"6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b","startServerTime":"653e45484ae81d4c65b29a8e66231fbab74c7a91e8faf4209b7f6be9196cc944"}
    //{"sessionId":"6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b","startServerTime":"653e45484ae81d4c65b29a8e66231fbab74c7a91e8faf4209b7f6be9196cc944"}
    @Test
    public void testChater() throws Exception {
        chater.webSocketText(new ReceiveMessage(FAKE_SESSION, FAKE_SERVER_TIME, FAKE_TEXT));
    }


}
