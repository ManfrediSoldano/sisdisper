package sisdisper.client;

import java.util.Observable;

import sisdisper.client.model.action.Action;

public class UserObservable extends Observable {

	public void setActionChanged(Action action){
		 setChanged();
         notifyObservers(action);  
	}
}
