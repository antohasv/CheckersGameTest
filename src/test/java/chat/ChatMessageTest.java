package chat;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ChatMessageTest {

    public static final String FAKE_TEXT = "fake_text";
    public static final String FAKE_SENDER = "fake_sender";

    @Test
    public void testMessageFormat() throws Exception {
        ChatMessage chatMessage = new ChatMessage(FAKE_SENDER, FAKE_TEXT);
        String json = chatMessage.getJson();
        Assert.assertEquals(json, "{\"sender\":\"" + FAKE_SENDER + "\",\"text\":\"" + FAKE_TEXT + "\"}");
    }
}
