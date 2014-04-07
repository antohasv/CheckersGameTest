package gameMechanic;

import base.Abonent;
import base.Address;
import base.MessageSystem;
import dbService.DBServiceImpl;
import dbService.UserDataSet;
import frontend.UserDataImpl;
import frontend.WebSocketImpl;
import gameClasses.Stroke;
import gameMechanic.Stroke.MsgCheckStroke;
import gameMechanic.gameCreating.MsgUpdateColors;
import messageSystem.MessageSystemImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.testng.Assert;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MsgCheckStrokeTest {
    public static final int toX = 1;
    public static final int toY = 2;
    public static final int fromX = 3;
    public static final int fromY = 4;
    public static final String status = "OK";

    private UserDataSet userSession;
    private MessageSystem messageSystem;
    private GameMechanicImpl gameMechanic;
    private WebSocketImpl webSocket;

    @Before
    public  void setUp() throws Exception {
        userSession = mock(UserDataSet.class);
        messageSystem = new MessageSystemImpl();
        gameMechanic = new GameMechanicImpl(messageSystem);
        webSocket = new WebSocketImpl(messageSystem);
    }

    @Test
    public void checkStrokeTest() throws Exception {
        Address address = webSocket.getAddress();
        Address to = messageSystem.getAddressByName("WebSocket");
        Stroke stroke = new Stroke(toX, toY, fromX, fromY, status);
        when(userSession.getId()).thenReturn(1);
        MsgCheckStroke msg = new MsgCheckStroke(address, to, userSession.getId(), stroke);
        messageSystem.putMsg(to, msg);
        Assert.assertTrue(messageSystem.getMessages().get(to).contains(msg));
        Assert.assertFalse(msg.exec((Abonent) webSocket));
        Assert.assertTrue(msg.exec((Abonent) gameMechanic));
    }

}
