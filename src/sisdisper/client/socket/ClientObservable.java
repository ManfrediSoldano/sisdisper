package sisdisper.client.socket;
import java.util.Observable;

import sisdisper.client.model.action.Action;

public class ClientObservable extends Observable {

	public void setActionChanged(Action action){
		System.out.println("###ClientObservable## Action received: "+action.toString()+" ##");
		 setChanged();
         notifyObservers(action);  
	}
}
