package gameMechanic;

import base.Abonent;
import base.Address;
import base.MessageSystem;
import dbService.UserDataSet;
import frontend.WebSocketImpl;
import gameClasses.Field;
import gameClasses.Snapshot;
import gameClasses.Stroke;
import gameMechanic.Stroke.MsgCheckStroke;
import gameMechanic.Stroke.MsgDoneSnapshot;
import messageSystem.MessageSystemImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MsgDoneSnapshotTest {
    public static final int TEST_USER_ID = 100500;
    private Map<String, UserDataSet> users;
    private Snapshot snapshot;
    private GameSession gameSession;
    private UserDataSet userSession;
    private MessageSystem messageSystem;
    private GameMechanicImpl gameMechanic;
    private WebSocketImpl webSocket;

    @BeforeMethod
    public  void setUp() throws Exception {
        gameSession = new GameSession(TEST_USER_ID, TEST_USER_ID);
        users = new HashMap<String, UserDataSet>();
        snapshot = mock(Snapshot.class);
        userSession = mock(UserDataSet.class);
        messageSystem = new MessageSystemImpl();
        gameMechanic = new GameMechanicImpl(messageSystem);
        webSocket = new WebSocketImpl(messageSystem);
        users.put("100500", userSession);
    }

    @Test
    public void snapshotTest() throws Exception {
        Address address = gameMechanic.getAddress();
        Address to = messageSystem.getAddressByName("GameMechanic");
        gameMechanic.userIdToSession.put(TEST_USER_ID, gameSession);
        gameMechanic.createGames(users);
        snapshot = gameMechanic.getSnapshot(TEST_USER_ID);
        MsgDoneSnapshot msg = new MsgDoneSnapshot(address, to, TEST_USER_ID, snapshot);
        messageSystem.putMsg(to, msg);
        Assert.assertTrue(messageSystem.getMessages().get(to).contains(msg));
        Assert.assertFalse(msg.exec((Abonent) gameMechanic));
    }
}
