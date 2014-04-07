package gameMechanic;

import base.Abonent;
import base.Address;
import base.MessageSystem;
import dbService.UserDataSet;
import frontend.UserDataImpl;
import frontend.WebSocketImpl;
import gameMechanic.gameCreating.MsgCreateGames;
import org.junit.runner.RunWith;
import org.testng.Assert;
import messageSystem.MessageSystemImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RunWith(MockitoJUnitRunner.class)
public class MsgCreateGamesTest {
    private MessageSystem messageSystem;
    @Mock private UserDataImpl userData;
    private GameMechanicImpl gameMechanic;
    private WebSocketImpl webSocket;
    private Map<String, UserDataSet> sendMap;

    @BeforeMethod
    public  void setUp() throws Exception {
        messageSystem = new MessageSystemImpl();
        gameMechanic = new GameMechanicImpl(messageSystem);
        webSocket = new WebSocketImpl(messageSystem);
        sendMap = new ConcurrentHashMap<String, UserDataSet>();
    }

    @Test
    public void createGamesTest() throws Exception {
        Address address = gameMechanic.getAddress();
        Address to = messageSystem.getAddressByName("GameMechanic");
        MsgCreateGames msg = new MsgCreateGames(address, to, sendMap);
        messageSystem.putMsg(to, msg);
        Assert.assertTrue(messageSystem.getMessages().get(to).contains(msg));
        Assert.assertTrue(msg.exec((Abonent) gameMechanic));
        Assert.assertFalse(msg.exec((Abonent) webSocket));
    }
}
