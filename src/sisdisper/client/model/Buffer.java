package sisdisper.client.model;

import java.util.ArrayList;


import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.Action;

import sisdisper.client.model.action.AskPosition;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.MoveCLI;
import sisdisper.client.model.action.MoveCom;
import sisdisper.client.model.action.NewPlayer;
import sisdisper.client.model.action.NewPlayerResponse;
import sisdisper.client.model.action.WelcomeNewPlayer;
import sisdisper.client.socket.Client;

public class Buffer {
	private static BufferController bufferController;
	private static Buffer instance = null;

	public static Buffer getIstance() {
		if (instance == null) {
			instance = new Buffer();
		}
		return instance;
	}

	private static ArrayList<Action> actions = new ArrayList<Action>();
	private static ArrayList<Action> actionsThatNeedsAToken = new ArrayList<Action>();

	
	
	
	
	public synchronized Boolean addAction(Action action) {

		if (!(action instanceof MoveCLI) && !(action instanceof Bomb) && !(action instanceof NewPlayer) ) {
			actions.add(action);
			synchronized (bufferController) {
				bufferController.notify();
			}
			return true;
		} else {

			if((action instanceof NewPlayer)){
				synchronized(actions){
					actions.add(action);
				}
				

			} else {
			actionsThatNeedsAToken.add(action);
			}
		
			return true;
		}
	}

	public synchronized Boolean addAction(Action action, Client client) throws JAXBException, InterruptedException, JsonProcessingException {
		//System.out.println("##BUFFER### AddAction (Action action, Client client) #####");
		if(!(action instanceof NewPlayerResponse) ){
		if (action instanceof WelcomeNewPlayer) {
			System.out.println("##BUFFER### WELCOME NEW PLAYER #####");
			for (Action deleteAction : actions) {
				if (deleteAction instanceof NewPlayer) {
					if (((NewPlayer) deleteAction).getPlayer().getId().equals(((WelcomeNewPlayer) action).getNewPlayer().getId())) {
						System.out.println("##BUFFER### REMOVED NEWPLAYER #####");
						synchronized(actions){
							ArrayList<Action> actions_new = new ArrayList<Action>();
							actions_new.remove(action);
							actions=actions_new;
						}
						Ack ack = new Ack();
						ack.setPlayer(((WelcomeNewPlayer) action).getNewPlayer());
						client.send(ack);
					}
				}
				
				
				
			}
		}
		
		
		
		
		if(action instanceof AskPosition){
			((AskPosition) action).setClient(client);
		}
		if(action instanceof MoveCom){
			((MoveCom) action).setClient(client);
		}
		
		synchronized(actions){
			//TimeUnit.SECONDS.sleep(5);
			actions.add(action);
		}
		

		synchronized (bufferController) {
			bufferController.notify();
		}
		return true;
		}
		else{
			synchronized(actions){
				actions.add(action);
				return true;
			}
		}
	}

	public synchronized Action getFirstActionThatNeedAToken() {
		
		if (!actionsThatNeedsAToken.isEmpty()) {
			
			Action action = actionsThatNeedsAToken.get(0);
			actionsThatNeedsAToken.remove(0);
			return action;

		}

		return null;
	}

	public synchronized Action getFirstAction() {
	
		if (!actions.isEmpty()) {
			System.out.println("##BUFFER### Actions: #####");
			for(Action action:actions){
				System.out.println("##BUFFER### "+action.getClass()+" #####");
			}
			synchronized(actions){
			Action action = actions.get(0);
			actions.remove(0);
			return action;
			}

		}

		return null;
	}

	public synchronized ArrayList<Action> getAllActions() {
		
		return actions;
	}

	public synchronized BufferController getBufferController() {
		return bufferController;
	}

	public synchronized void setBufferController(BufferController bufferControll) {
		bufferController = bufferControll;
	}
	
	public synchronized void deleteAction(Action action) {
		synchronized(actions){
			ArrayList<Action> actions_new = new ArrayList<Action>();
			actions_new.remove(action);
			actions=actions_new;
		}
	}

}
