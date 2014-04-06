package gameMechanic;

import base.GameChat;
import base.GameMechanic;
import base.MessageSystem;
import chat.GameChatImpl;
import dbService.UserDataSet;
import frontend.FrontendImpl;
import frontend.UserDataImpl;
import gameClasses.Stroke;
import messageSystem.MessageSystemImpl;
import org.testng.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameMechanicImplTest {
    public static final int toX = 0;
    public static final int toY = 3;
    public static final int fromX = 0;
    public static final int fromY = 5;
    public static final String status = "OK";
    public static final int id = 1;
    public static final String USER_ID_1 = "1";
    public static final String USER_ID_2 = "2";
    public static final String USER_ID_3 = "3";

    final MessageSystem messageSystem = new MessageSystemImpl();
    public GameMechanicImpl gameMechanic = new GameMechanicImpl(messageSystem);
    final GameChat gameChat = new GameChatImpl(messageSystem);

    @Before
    public  void setUp() throws Exception {

    }

    @Test
    public void twoUsersTest() throws Exception {
        UserDataSet userDataSet1 = mock(UserDataSet.class);
        UserDataSet userDataSet2 = mock(UserDataSet.class);
        when(userDataSet1.getId()).thenReturn(1);

        when(userDataSet2.getId()).thenReturn(2);
        Map<String, UserDataSet> users = new ConcurrentHashMap<String, UserDataSet>();
        users.put(USER_ID_1, userDataSet1);
        users.put(USER_ID_2, userDataSet2);

        UserDataImpl.putLogInUser(USER_ID_1, userDataSet1);
        UserDataImpl.putLogInUser(USER_ID_2, userDataSet2);

        Map<String,String> sessionIdToColor = gameMechanic.createGames(users);

        Assert.assertTrue(users.isEmpty());
        Assert.assertTrue(gameMechanic.getWantToPlay().isEmpty());

        Map<Integer, Stroke> resp;
        Stroke stroke;
        int id;
        if(sessionIdToColor.get(USER_ID_1) == "white")
        {
            stroke = new Stroke(toX, toY, fromX, fromY, status);
            id = userDataSet1.getId();
        }
        else
        {
            stroke = new Stroke(toX, toY, fromX, fromY, status);
            id = userDataSet2.getId();
        }
        resp = gameMechanic.checkStroke(id, stroke);
        Assert.assertEquals(resp.get(id), stroke);
        gameMechanic.removeDeadGames();
    }

    @Test
    public void threeUsersTest() throws Exception {
        UserDataSet userDataSet1 = mock(UserDataSet.class);
        UserDataSet userDataSet2 = mock(UserDataSet.class);
        UserDataSet userDataSet3 = mock(UserDataSet.class);

        when(userDataSet1.getId()).thenReturn(1);
        when(userDataSet2.getId()).thenReturn(2);
        when(userDataSet2.getId()).thenReturn(3);

        Map<String, UserDataSet> users = new ConcurrentHashMap<String, UserDataSet>();
        users.put(USER_ID_1, userDataSet1);
        users.put(USER_ID_2, userDataSet2);
        users.put(USER_ID_3, userDataSet3);

        UserDataImpl.putLogInUser(USER_ID_1, userDataSet1);
        UserDataImpl.putLogInUser(USER_ID_2, userDataSet2);
        UserDataImpl.putLogInUser(USER_ID_3, userDataSet3);

        Map<String,String> sessionIdToColor = gameMechanic.createGames(users);
        Assert.assertEquals(gameMechanic.getWantToPlay().size(), 1);
        Assert.assertTrue(users.isEmpty());

        Map<Integer, Stroke> resp;
        Stroke stroke;
        int id;
        if(sessionIdToColor.get(USER_ID_1) == "white")
        {
            stroke = new Stroke(toX, toY, fromX, fromY, status);
            id = userDataSet1.getId();
        }
        else if(sessionIdToColor.get(USER_ID_2) == "white")
        {
            stroke = new Stroke(toX, toY, fromX, fromY, status);
            id = userDataSet2.getId();
        }
        else
        {
            stroke = new Stroke(toX, toY, fromX, fromY, status);
            id = userDataSet3.getId();
        }
        resp=gameMechanic.checkStroke(id, stroke);
        Assert.assertEquals(resp.get(id), stroke);
        gameMechanic.removeDeadGames();
    }

}
