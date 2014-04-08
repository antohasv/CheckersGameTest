package gameMechanic.Stroke;

import base.Address;
import base.UserData;
import messageSystem.MsgToUserData;

public class MsgPartyEnd extends MsgToUserData{
	int winId, loseId;
	
	public MsgPartyEnd(Address from, Address to, int winId, int loseId){
		super(from,to);
		this.winId = winId;
		this.loseId = loseId;
	}
	
	public void exec(UserData userData){ userData.partyEnd(winId, loseId); }
}
