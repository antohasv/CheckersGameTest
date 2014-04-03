package chat;

import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChatWebSocketImplTest {
    @Test
    public void testonWebSocketText() throws Exception {
        ChatWebSocketImpl chatWebSocket = mock(ChatWebSocketImpl.class);
        when(chatWebSocket.isNotConnected()).thenReturn(false);
        chatWebSocket.onWebSocketText("Text");
    }
}
