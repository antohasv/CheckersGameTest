package dbService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBUserManager {
    public static final String TABLE_NAME = "Users";

    public static final String COL_ID = "id";
    public static final String COL_NICKNAME = "nickname";
    public static final String COL_PASSWORD = "password";
    public static final String COL_REGISTRATION_DATE = "registration_date";
    public static final String COL_RATING = "rating";
    public static final String COL_WIN_QUANTITY = "win_quantity";
    public static final String COL_LOSE_QUANTITY = "lose_quantity";
    public static final String USERS_COUNT = "users_count";

    public static void addUser(Connection connection, String login, String password) {
        PreparedStatement statement = null;

        StringBuilder query = new StringBuilder("INSERT INTO ").append(TABLE_NAME);
        query.append("(").append(COL_NICKNAME).append(",").append(COL_PASSWORD)
                .append(",").append(COL_REGISTRATION_DATE).append(")");
        query.append(" VALUES(?,?,CURRENT_TIMESTAMP)");

        try {
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, login);
            statement.setString(2, password);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            System.err.println("\nError");
            System.err.println(e.getMessage());
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int findUser(Connection connection, String login) {
        int rows = 0;
        PreparedStatement stmt = null;
        StringBuilder query = new StringBuilder("SELECT COUNT(*) as ").append(USERS_COUNT).append(" FROM ");
        query.append(TABLE_NAME).append(" WHERE nickname=?");
        try {
            stmt = connection.prepareStatement(query.toString());
            stmt.setString(1, login);
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();
            if (resultSet.first())
                rows = resultSet.getInt(USERS_COUNT);
            stmt.close();
        } catch (Exception e) {
            System.err.println("\nError");
            System.err.println(e.getMessage());
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rows;
    }

    public static <T> T getUserData(Connection connection, String login, String password,
                                    TResultHandler<T> handler) {
        PreparedStatement stmt = null;
        T user = null;

        StringBuilder query = new StringBuilder("SELECT ").append(COL_ID).append(",").append(COL_NICKNAME)
                .append(",").append(COL_RATING).append(",").append(COL_WIN_QUANTITY).append(",").append(COL_LOSE_QUANTITY);
        query.append(" FROM ").append(TABLE_NAME).append(" WHERE nickname=? AND password=?");

        try {
            stmt = connection.prepareStatement(query.toString());
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();
            user = handler.handle(resultSet);
            stmt.close();
        } catch (Exception e) {
            System.err.println("\nError");
            System.err.println(e.getMessage());
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public static void updateUser(Connection connection, String login,
                                  int rating, int winQuantity, int loseQuantity) {
        PreparedStatement stmt = null;

        StringBuilder query = new StringBuilder("UPDATE ").append(TABLE_NAME);
        query.append(" SET ").append(COL_RATING).append(" = ?, ").append(COL_WIN_QUANTITY).append(" = ?, ")
                .append(COL_LOSE_QUANTITY).append(" = ?");
        query.append(" WHERE ").append(COL_NICKNAME).append(" = ?");

        try {
            stmt = connection.prepareStatement(query.toString());
            stmt.setInt(1, rating);
            stmt.setInt(2, winQuantity);
            stmt.setInt(3, loseQuantity);
            stmt.setString(4, login);
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            System.err.println("\nError");
            System.err.println(e.getMessage());
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}