package base;

import dbService.UserDataSet;

public interface UserData extends Abonent,Runnable{
	public void updateUserId(String sessionId, UserDataSet user);
	public void partyEnd(int winId, int loseId);
}
