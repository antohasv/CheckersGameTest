package dbService;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;

import utils.TimeHelper;
import base.Address;
import base.DataAccessObject;
import base.MessageSystem;

public class DBServiceImpl implements DataAccessObject {
    public static final String DB_SERVICE = "DBService";

    public static final String DB = "mysql";
    public static final String DB_HOST = "localhost";
    public static final String DB_PORT = "3306";

    public static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    public static final String DB_NAME = "checkers";
    public static final String PARAM_USER = "user";
    public static final String PARAM_PASSWORD = "password";
    public static final String USER_NAME = "checkers";
    public static final String USER_PASSWORD = "QSQ9D9BUBW93DK8A7H9FPXOB5OLOP84BA4CJRWK96VN0GPVC6P";

    public static final int TICK_TIME = 200;

    private final MessageSystem messageSystem;
    private final Address address;
    private Connection connection;

    public DBServiceImpl(MessageSystem msgSystem) {
        address = new Address();
        messageSystem = msgSystem;
        messageSystem.addService(this, DB_SERVICE);
    }

    public MessageSystem getMessageSystem() {
        return messageSystem;
    }

    public Address getAddress() {
        return address;
    }

    public UserDataSet getUserData(final String login, String password) {
        UserDataSet user = DBUserManager.getUserData(connection, login, password,
                new TResultHandler<UserDataSet>() {
                    @Override
                    public UserDataSet handle(ResultSet result) {
                        try {
                            if (result.first()) {
                                int id = result.getInt(DBUserManager.COL_ID);
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
                });
        return user;
    }

    public boolean addUserData(final String login, String password) {
        int rows = DBUserManager.findUser(connection, login);
        if (rows == 0)
            DBUserManager.addUser(connection, login, password);
        return (rows == 0);
    }

    public void updateUsers(List<UserDataSet> users) {
        ListIterator<UserDataSet> li = users.listIterator();
        while (li.hasNext()) {
            UserDataSet user = li.next();
            String login = user.getNick();
            int rating = user.getRating();
            int winQuantity = user.getWinQuantity();
            int loseQuantity = user.getLoseQuantity();
            DBUserManager.updateUser(connection, login, rating, winQuantity, loseQuantity);
        }
    }

    public void run() {
        createDatabase();
        while (true) {
            messageSystem.execForAbonent(this);
            TimeHelper.sleep(TICK_TIME);
        }
    }

    private void createDatabase() {
        try {
            Driver driver = (Driver) Class.forName(DB_CLASS_NAME).newInstance();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(getJDBCURI());
        } catch (Exception e) {
            obtainServiceExcpetion(e);
        }
    }

    private void obtainServiceExcpetion(Exception e) {
        System.err.println(e.getMessage());
        System.exit(-1);
    }

    public static String getJDBCURI() {
        StringBuilder sb = new StringBuilder("jdbc:").append(DB).append("://");
        sb.append(DB_HOST).append(":").append(DB_PORT);
        sb.append("/").append(DB_NAME);
        sb.append("?").append(PARAM_USER).append("=").append(USER_NAME);
        sb.append("&").append(PARAM_PASSWORD).append("=").append(USER_PASSWORD);
        return sb.toString();
    }
}
