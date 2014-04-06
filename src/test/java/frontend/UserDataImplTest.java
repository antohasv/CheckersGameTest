package frontend;

import base.MessageSystem;
import base.UserData;
import dbService.UserDataSet;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

public class UserDataImplTest {

    public static final String FAKE_SESSION_ID = "fake_session_id";
    public static final int FAKE_USER_ID = 100;
    private UserDataImpl userData;
    private MessageSystem messageSystem;

    @BeforeMethod
    public void setUp() throws Exception {
        messageSystem = mock(MessageSystem.class);
        userData = new UserDataImpl(messageSystem);
    }

    @Test
    public void testGetSession() throws Exception {
        UserDataImpl.putLogInUser(FAKE_SESSION_ID, new UserDataSet(FAKE_USER_ID, "FAKE_NAME", 0, 0, 0));
        Assert.assertEquals(UserDataImpl.getSessionIdByUserId(FAKE_USER_ID), FAKE_SESSION_ID);
    }
}
