package messageSystem;

import base.Abonent;
import base.Address;
import base.GameMechanic;
import base.Msg;


public abstract class MsgToGameMechanic extends Msg{

	public MsgToGameMechanic(Address from, Address to){
		super(from,to);
	}

	public boolean exec(Abonent abonent){
		if (abonent instanceof GameMechanic){
			exec((GameMechanic)abonent);
            return true;
		}
        else
            return false;
	}
	public abstract void exec(GameMechanic gameMechanic);
}