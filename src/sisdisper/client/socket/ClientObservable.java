package sisdisper.client.socket;
import java.util.Observable;

import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.PassToken;

public class ClientObservable extends Observable {

	public void setActionChanged(Action action){
		
		 setChanged();
         notifyObservers(action);  
	}
}
