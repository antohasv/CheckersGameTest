package gameMechanic;

import base.Address;
import base.MessageSystem;
import dbService.UserDataSet;
import frontend.UserDataImpl;
import gameMechanic.gameCreating.MsgCreateGames;
import org.junit.runner.RunWith;
import org.testng.Assert;
import messageSystem.MessageSystemImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(MockitoJUnitRunner.class)
public class MsgCreateGamesTest {
    private MessageSystem messageSystem;
    @Mock private UserDataImpl userData;
    private GameMechanicImpl gameMechanic;
    private Map<String, UserDataSet> sendMap;

    @Before
    public  void setUp() throws Exception {
        messageSystem = new MessageSystemImpl();
        gameMechanic = new GameMechanicImpl(messageSystem);
        sendMap = new ConcurrentHashMap<String, UserDataSet>();
    }

    @Test
    public void createGamesTest() throws Exception {
        Address address = userData.getAddress();
        Address to = messageSystem.getAddressByName("GameMechanic");
        MsgCreateGames msg = new MsgCreateGames(address, to, sendMap);
        messageSystem.putMsg(to, msg);
        Assert.assertTrue(messageSystem.getMessages().get(to).contains(msg));
    }


}
