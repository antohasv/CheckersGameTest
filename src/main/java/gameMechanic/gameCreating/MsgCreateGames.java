package gameMechanic.gameCreating;

import java.util.Map;

import dbService.UserDataSet;

import base.Address;
import base.GameMechanic;
import messageSystem.MsgToGameMechanic;


public class MsgCreateGames extends MsgToGameMechanic{
	final private Map<String, UserDataSet> users;

	public MsgCreateGames(Address from, Address to, Map<String, UserDataSet> data){
		super(from,to);
		users=data;
	}

	public void exec(GameMechanic gameMechanic){
		Map<String,String> sessionIdToColor=gameMechanic.createGames(users);
		Address to=gameMechanic.getMessageSystem().getAddressByName("WebSocket");
		MsgUpdateColors msg=new MsgUpdateColors(gameMechanic.getAddress(),to,sessionIdToColor);
		gameMechanic.getMessageSystem().putMsg(to, msg);
	}
}