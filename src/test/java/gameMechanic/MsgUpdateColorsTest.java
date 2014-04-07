package gameMechanic;

import base.Abonent;
import base.Address;
import base.MessageSystem;
import chat.GameChatImpl;
import frontend.UserDataImpl;
import frontend.WebSocketImpl;
import gameMechanic.gameCreating.MsgCreateChat;
import gameMechanic.gameCreating.MsgUpdateColors;
import messageSystem.MessageSystemImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.Mock;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

public class MsgUpdateColorsTest {
    public static Map<String,String> testSessionIdToColor;
    public static final String TEST_SESSION_ID = "1";
    public static final String TEST_COLOR = "red";


    private MessageSystem messageSystem;
    private GameMechanicImpl gameMechanic;
    private WebSocketImpl webSocket;

    @BeforeMethod
    public  void setUp() throws Exception {
        messageSystem = new MessageSystemImpl();
        gameMechanic = new GameMechanicImpl(messageSystem);
        webSocket = new WebSocketImpl(messageSystem);
        testSessionIdToColor = new HashMap<String, String>();
    }

    @Test
    public void updateColorsTest() throws Exception {
        Address address = gameMechanic.getAddress();
        Address to = messageSystem.getAddressByName("WebSocket");
        testSessionIdToColor.put(TEST_SESSION_ID, TEST_COLOR);
        MsgUpdateColors msg = new MsgUpdateColors(address, to, testSessionIdToColor);
        messageSystem.putMsg(to, msg);
        Assert.assertTrue(messageSystem.getMessages().get(to).contains(msg));
        Assert.assertFalse(msg.exec((Abonent) gameMechanic));
        Assert.assertTrue(msg.exec((Abonent) webSocket));
    }
}
