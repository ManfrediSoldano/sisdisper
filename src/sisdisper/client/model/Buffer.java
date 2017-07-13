package sisdisper.client.model;

import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;

import sisdisper.client.model.action.Action;

import sisdisper.client.model.action.AskPosition;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.MoveCLI;
import sisdisper.client.model.action.MoveCom;
import sisdisper.client.model.action.NewPlayer;
import sisdisper.client.model.action.NewPlayerResponse;
import sisdisper.client.model.action.PassToken;
import sisdisper.client.model.action.ResponseMove;
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

	public synchronized static Boolean addAction(Action action) {
		synchronized (actionsThatNeedsAToken) {
			
			if (!(action instanceof MoveCLI) && !(action instanceof Bomb) && !(action instanceof NewPlayer)) {
				synchronized (actions) {
				
				

				actions.add(action);

					synchronized (bufferController) {
						while(!bufferController.imFree){
						bufferController.notify();
						}
					}
				}
				return true;
			} else {

				if ((action instanceof NewPlayer)) {

					System.out.println("##BUFFER### ADDED ON BUFFER: " + action.getClass() + " #####");
					actionsThatNeedsAToken.add(action);

				} else {
					actionsThatNeedsAToken.add(action);
				}
			}
			
			return true;
		}
	}

	public synchronized static Boolean addAction(Action action, Client client)
			throws JAXBException, InterruptedException, JsonProcessingException {
		
		if (!(action instanceof NewPlayerResponse) && !(action instanceof NewPlayer)) {
			synchronized (bufferController) {
			if (action instanceof WelcomeNewPlayer) {

				for (Action deleteAction : actionsThatNeedsAToken) {
					if (deleteAction instanceof NewPlayer) {
						if (((NewPlayer) deleteAction).getPlayer().getId().equals(((WelcomeNewPlayer) action).getNewPlayer().getId())) {

							Boolean removed = actionsThatNeedsAToken.remove(deleteAction);
							System.out.println("##BUFFER### REMOVED NEWPLAYER: "+removed+"#####");
							break;
							/*
							 * Ack ack = new Ack();
							 * ack.setPlayer(((WelcomeNewPlayer)
							 * action).getNewPlayer()); client.send(ack);
							 */
						}
					}

				}
			}
			
			if (action instanceof AskPosition) {
				((AskPosition) action).setClient(client);
			}
			if (action instanceof MoveCom) {
				((MoveCom) action).setClient(client);
			}
			
			if (action instanceof ResponseMove) {
				System.out.println("##BUFFER### RECEIVED A RESPONSE MOVE FROM : "+((ResponseMove)action).getPlayer().getId()+" #####");
			}

			
				// TimeUnit.SECONDS.sleep(5);
			if (action instanceof WelcomeNewPlayer) {
					System.out.println("##BUFFER### ADDED TO THE BUFFER A WELCOMETOPLAYER: SENDER: "
							+ ((WelcomeNewPlayer) action).getSender().getId() + " NEW PLAYER: "
							+ ((WelcomeNewPlayer) action).getNewPlayer().getId() + " #####");
			}
			
			if (action instanceof PassToken) {
				//System.out.println("##BUFFER### RECEIVED A PASS TOKEN #####");
			}
			actions.add(action);
			

			while(!bufferController.imFree){
			bufferController.notify();
			}
			
			return true;
			}
			
			
		}

		else {

			System.out.println("##BUFFER### ADDED ON BUFFER: " + action.getClass() + " #####");
			actionsThatNeedsAToken.add(action);

		}
		return true;

	}

	public static synchronized Action getFirstActionThatNeedAToken() {
		synchronized (actionsThatNeedsAToken) {
			if (!actionsThatNeedsAToken.isEmpty()) {

				Action action = actionsThatNeedsAToken.get(0);
				if (!(action instanceof NewPlayer) && !(action instanceof NewPlayerResponse)) {
					actionsThatNeedsAToken.remove(0);
					return action;
				}

			}
		}
		return null;
	}

	public synchronized Action getFirstAction() {

		if (!actions.isEmpty()) {
			Action action = new Action();
			synchronized (actions) {
				action = actions.get(0);
				actions.remove(0);
			}
			return action;

		}

		return null;
	}

	public synchronized ArrayList<Action> getAllActions() {

		return actions;
	}

	public synchronized ArrayList<Action> getAllActionsThatNeedsAToken() {
		synchronized (actionsThatNeedsAToken) {
			if (actionsThatNeedsAToken.size() > 0) {
				// System.out.println("##BUFFER### getAllActionsThatNeedsAToken:
				// SIZE LARGER THAN zero#####");

			}
			return actionsThatNeedsAToken;
		}
	}

	public synchronized BufferController getBufferController() {
		return bufferController;
	}

	public synchronized void setBufferController(BufferController bufferControll) {
		bufferController = bufferControll;
	}

	public static synchronized void deleteAction(Action action) {
		synchronized (actionsThatNeedsAToken) {

			actionsThatNeedsAToken.remove(action);

		}
	}

}
