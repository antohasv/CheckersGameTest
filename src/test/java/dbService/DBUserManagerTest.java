package dbService;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.Random;

public class DBUserManagerTest {
    public static final String PREFIX_FAKE_USER = "fake_user";
    public static final String FAKE_PASSWORD = "fake_password";
    public static final int RANGE_USER_NUMBER = 10000;
    public static final int FAKE_RAITING = 10;
    public static final int FAKE_WIN_QUANTITY = 100;
    public static final int FAKE_LOSE_QUANTITY = 50;

    private Connection connection;

    private TResultHandler resultHandler = new TResultHandler<Object>() {
        @Override
        public Object handle(ResultSet result) {
            try {
                if (result.first()) {
                    int id = result.getInt(DBUserManager.COL_ID);
                    String login = result.getString(DBUserManager.COL_NICKNAME);
                    int rating = result.getInt(DBUserManager.COL_RATING);
                    int winQuantity = result.getInt(DBUserManager.COL_WIN_QUANTITY);
                    int loseQuantity = result.getInt(DBUserManager.COL_LOSE_QUANTITY);
                    return new UserDataSet(id, login, rating, winQuantity, loseQuantity);
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            return null;
        }
    };

    @BeforeMethod
    public void setUp() {
        try {
            Driver driver = (Driver) Class.forName(DBServiceImpl.DB_CLASS_NAME).newInstance();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(DBServiceImpl.getJDBCURI());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testAddUser() throws Exception {
        DBUserManager.addUser(connection, getUserName(), FAKE_PASSWORD);
        int userCount = DBUserManager.findUser(connection, PREFIX_FAKE_USER);
        Assert.assertEquals(userCount, 1);
    }

    @Test
    public void testGetUser() throws Exception {
        final String fakeName = getUserName();

        DBUserManager.addUser(connection, fakeName, FAKE_PASSWORD);
        UserDataSet userDataSet = (UserDataSet) DBUserManager.getUserData(connection, fakeName, FAKE_PASSWORD, resultHandler);

        Assert.assertNotNull(userDataSet);
        Assert.assertEquals(fakeName, userDataSet.getNickName());
    }

    @Test
    public void testUpdateUser() throws Exception {
        String fakeName = getFakeUserName();

        DBUserManager.addUser(connection, fakeName, FAKE_PASSWORD);
        DBUserManager.updateUser(connection, fakeName, FAKE_RAITING, FAKE_WIN_QUANTITY, FAKE_LOSE_QUANTITY);
        UserDataSet userDataSet = (UserDataSet) DBUserManager.getUserData(connection, fakeName, FAKE_PASSWORD, resultHandler);

        Assert.assertNotNull(userDataSet);
        Assert.assertEquals(fakeName, userDataSet.getNickName());
        Assert.assertEquals(FAKE_RAITING, userDataSet.getRating());
        Assert.assertEquals(FAKE_WIN_QUANTITY, userDataSet.getWinQuantity());
        Assert.assertEquals(FAKE_LOSE_QUANTITY, userDataSet.getLoseQuantity());
    }

    public String getUserName() {
        String fakeName;
        int userCount;
        do {
            fakeName = getFakeUserName();
            userCount = DBUserManager.findUser(connection, fakeName);
        } while(userCount != 0);
        return fakeName;
    }

    public String getFakeUserName() {
        Random rand = new Random();
        return PREFIX_FAKE_USER + (rand.nextInt() % RANGE_USER_NUMBER);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        connection.close();
    }
}
