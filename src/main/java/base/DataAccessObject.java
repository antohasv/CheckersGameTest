package base;

import java.util.List;

import dbService.UserDataSet;

public interface DataAccessObject extends Abonent,Runnable{
	public MessageSystem getMessageSystem();
    public void createConnection();
	public UserDataSet getUserData(final String login, String password);
	public boolean addUserData(final String login, String password);
	public void updateUsers(List<UserDataSet> users);
    public int deleteUser(String login);
}