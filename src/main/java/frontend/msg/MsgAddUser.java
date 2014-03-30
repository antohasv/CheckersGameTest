package frontend.msg;

import dbService.UserDataSet;
import base.Address;
import base.DataAccessObject;
import messageSystem.MsgToDBService;


/**
 * Add user to db and update it in UserDataImpl
 * */
public class MsgAddUser extends MsgToDBService {
    final private String login;
    final private String sessionId;
    final private String password;

    public MsgAddUser(Address from, Address to, String sessionId, String nick, String password) {
        super(from, to);
        this.login = nick;
        this.sessionId = sessionId;
        this.password = password;
    }

    public void exec(DataAccessObject dbService) {
        UserDataSet userDataSet = null;

        if (dbService.addUserData(login, password)) {
            userDataSet = dbService.getUserData(login, password);
        }

        Address to = getFrom();
        MsgUpdateUser msg = new MsgUpdateUser(dbService.getAddress(), to, sessionId, userDataSet);
        dbService.getMessageSystem().putMsg(to, msg);
    }
}