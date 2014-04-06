package gameMechanic;

import base.Abonent;
import base.Address;
import base.MessageSystem;
import base.UserData;
import chat.GameChatImpl;
import dbService.UserDataSet;
import frontend.UserDataImpl;
import gameMechanic.Stroke.MsgPartyEnd;
import gameMechanic.gameCreating.MsgCreateChat;
import messageSystem.MessageSystemImpl;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

import static org.mockito.Mockito.mock;

public class MsgPartyEndTest {
    public static final int WINNER_ID = 1;
    public static final int LOSER_ID = 2;
    private MessageSystem messageSystem;
    private GameMechanicImpl gameMechanic;
    private GameChatImpl gameChat;
    private UserDataImpl userData;

    @Before
    public  void setUp() throws Exception {
        messageSystem = new MessageSystemImpl();
        gameMechanic = new GameMechanicImpl(messageSystem);
        gameChat = new GameChatImpl(messageSystem);
        userData = new UserDataImpl(messageSystem);
    }

    @Test
    public void partyEndTest() throws Exception {
        Address address = gameMechanic.getAddress();
        Address to = messageSystem.getAddressByName("UserDataSet");


        UserDataSet userDataSet1 = new UserDataSet();
        UserDataSet userDataSet2 = new UserDataSet();
        userData.putLogInUser("1", userDataSet1);
        userData.putLogInUser("2", userDataSet2);

        userData.putSessionIdAndUserSession("1", userDataSet1);
        userData.putSessionIdAndUserSession("2", userDataSet2);
        MsgPartyEnd msg = new MsgPartyEnd(address, to, WINNER_ID, LOSER_ID);

        messageSystem.putMsg(to, msg);
        Assert.assertTrue(messageSystem.getMessages().get(to).contains(msg));
        //Assert.assertTrue(msg.exec((Abonent) userData));
        Assert.assertFalse(msg.exec((Abonent) gameChat));
    }
}
