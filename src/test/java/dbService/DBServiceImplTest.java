package dbService;

import base.DataAccessObject;
import base.MessageSystem;
import base.UserData;
import messageSystem.MessageSystemImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DBServiceImplTest {
    public static final String FAKE_LOGIN = "fake_login";
    public static final String FAKE_LONG_LOGIN = "543545fjsdfreieuhhdiuiduih&DUHDipd39du3@#$%^&*()(*&^%$#$%^&*()";
    public static final String FAKE_PASSWORD = "fake_password";
    public static final int RAND_VALUE = 123;
    DataAccessObject dataAccessObject;

    @BeforeMethod
    public void setUp() throws Exception {
        MessageSystem messageSystem = new MessageSystemImpl();
        dataAccessObject = new DBServiceImpl(messageSystem);
        dataAccessObject.createConnection();
    }

    @Test
    public void testAddUser() throws Exception {
        Assert.assertTrue(dataAccessObject.addUserData(FAKE_LOGIN, FAKE_PASSWORD));
    }

    @Test
    public void testUpdateUsers() throws Exception {
        List<UserDataSet> users = createUsers();
        addUsers(users);
        changeUsersData(users);
        dataAccessObject.updateUsers(users);

        UserDataSet userDataSet = users.get(0);
        UserDataSet retrieveUser = dataAccessObject.getUserData(userDataSet.getNickName(), userDataSet.getNickName());
        Assert.assertNotNull(retrieveUser);
        Assert.assertEquals(retrieveUser.getRating(), RAND_VALUE);
    }

    private void addUsers(List<UserDataSet> users) {
        for (UserDataSet userDataSet : users) {
            dataAccessObject.addUserData(userDataSet.getNickName(), userDataSet.getNickName());
        }
    }

    public List<UserDataSet> createUsers() {
        List<UserDataSet> users = new ArrayList<UserDataSet>();
        for (int i = 0; i < 10; i++) {
            users.add(new UserDataSet(i, FAKE_LOGIN + i, i, i, i));
        }
        return users;
    }

    public void changeUsersData(List<UserDataSet> users) {
        for (UserDataSet userDataSet : users) {
            userDataSet.makeLike(new UserDataSet(userDataSet.getId(), userDataSet.getNickName(), RAND_VALUE, RAND_VALUE, RAND_VALUE));
        }
    }

    @AfterMethod
    public void tearDown() throws Exception {
        dataAccessObject.deleteUser(FAKE_LOGIN);
    }
}

