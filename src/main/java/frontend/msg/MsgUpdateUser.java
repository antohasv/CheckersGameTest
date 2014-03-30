package frontend.msg;

import dbService.UserDataSet;
import base.Address;
import messageSystem.MsgToUserData;


public class MsgUpdateUser extends MsgToUserData {
    final private String sessionId;
    final private UserDataSet userDataSet;

    public MsgUpdateUser(Address from, Address to, String sessionId, UserDataSet userDataSet) {
        super(from, to);
        this.sessionId = sessionId;
        this.userDataSet = userDataSet;
    }

    public void exec(base.UserData userData) {
        userData.updateUserId(sessionId, userDataSet);
    }
}