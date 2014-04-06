package gameMechanic;

import base.Abonent;
import base.Address;
import base.MessageSystem;
import chat.GameChatImpl;
import gameMechanic.gameCreating.MsgCreateChat;
import messageSystem.MessageSystemImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.testng.Assert;


public class MsgCreateChatTest {
    public static final String TEST_SESSION_1 = "1";
    public static final String TEST_SESSION_2 = "2";

    private MessageSystem messageSystem;
    private GameMechanicImpl gameMechanic;
    private GameChatImpl gameChat;

    @Before
    public  void setUp() throws Exception {
        messageSystem = new MessageSystemImpl();
        gameMechanic = new GameMechanicImpl(messageSystem);
        gameChat = new GameChatImpl(messageSystem);
    }

    @Test
    public void createChatTest() throws Exception {
        Address address = gameMechanic.getAddress();
        Address to = messageSystem.getAddressByName("GameMechanic");
        MsgCreateChat msg = new MsgCreateChat(address, to, TEST_SESSION_1, TEST_SESSION_2);
        messageSystem.putMsg(to, msg);
        Assert.assertTrue(messageSystem.getMessages().get(to).contains(msg));
        Assert.assertTrue(msg.exec((Abonent) gameChat));
        Assert.assertFalse(msg.exec((Abonent) gameMechanic));
    }
}
