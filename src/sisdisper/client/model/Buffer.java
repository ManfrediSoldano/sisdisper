package sisdisper.client.model;

import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;

import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AddBomb;
import sisdisper.client.model.action.AfterBombCheck;
import sisdisper.client.model.action.AskPosition;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.ExplodingBomb;
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
	public static ArrayList<Action> actionsThatNeedsAToken = new ArrayList<Action>();

	public  Boolean addAction(Action action) {

		if (!(action instanceof MoveCLI) && !(action instanceof Bomb) && !(action instanceof NewPlayer)
				&& !(action instanceof AddBomb)) {
			System.out.println("##BUFFER### INSIDE ADDACTION (ONLY ACTION) FROM " + action.getClass() + "#####");

			while (!bufferController.imFree) {
				
			}

			System.out.println("##BUFFER### FREE " + action.getClass() + "#####");
			synchronized (actions) {

				actions.add(action);
			}
			System.out.println("##BUFFER### NOTIFY " + action.getClass() + "#####");
			synchronized (bufferController) {
				bufferController.notify();
			}
			System.out.println("##BUFFER### OUTSIDE ADDACTION (ONLY ACTION) FROM " + action.getClass() + "#####");

			return true;
		} else {
			synchronized (actionsThatNeedsAToken) {
				if ((action instanceof NewPlayer)) {

					actionsThatNeedsAToken.add(action);
					System.out.println("##BUFFER### ADDED ON BUFFER (ONLY ACTION): " + action.getClass() + " #####");

				} else if ((action instanceof MoveCLI)) {

					actionsThatNeedsAToken.add(action);
					System.out.println("##BUFFER### ADDED ON BUFFER: " + action.getClass() + " #####");

				}

				else {
					actionsThatNeedsAToken.add(action);
				}
			}
		}

		return true;

	}

	public Boolean addAction(Action action, Client client)
			throws JAXBException, InterruptedException, JsonProcessingException {

		if (!(action instanceof PassToken)) {
			System.out.println("##BUFFER### INSIDE ADDACTION FROM " + action.getClass() + "#####");

		}

		if (!(action instanceof NewPlayerResponse) && !(action instanceof NewPlayer)
				&& !(action instanceof ExplodingBomb)) {
			synchronized (actions) {

				if (action instanceof WelcomeNewPlayer) {

					for (Action deleteAction : actionsThatNeedsAToken) {
						if (deleteAction instanceof NewPlayer) {
							if (((NewPlayer) deleteAction).getPlayer().getId()
									.equals(((WelcomeNewPlayer) action).getNewPlayer().getId())) {
								synchronized (actionsThatNeedsAToken) {
									Boolean removed = actionsThatNeedsAToken.remove(deleteAction);

									System.out.println("##BUFFER### REMOVED NEWPLAYER: " + removed + "#####");
								}
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

				if (action instanceof AfterBombCheck) {

					for (Action deleteAction : actionsThatNeedsAToken) {
						if (deleteAction instanceof ExplodingBomb) {
							if (((ExplodingBomb) deleteAction).player.getId()
									.equals(((AfterBombCheck) action).getPlayer().getId())) {
								synchronized (actionsThatNeedsAToken) {
									Boolean removed = actionsThatNeedsAToken.remove(deleteAction);

									System.out.println("##BUFFER### REMOVED Exploding Bomb: " + removed + "#####");
								}
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

			}
			if (action instanceof AskPosition) {
				((AskPosition) action).setClient(client);
			}
			if (action instanceof MoveCom) {
				((MoveCom) action).setClient(client);
			}

			if (action instanceof ResponseMove) {
				System.out.println("##BUFFER### RECEIVED A RESPONSE MOVE FROM : "
						+ ((ResponseMove) action).getPlayer().getId() + " #####");
			}

			// TimeUnit.SECONDS.sleep(5);

			if (action instanceof WelcomeNewPlayer) {
				System.out.println("##BUFFER### ADDED TO THE BUFFER A WELCOMETOPLAYER: SENDER: "
						+ ((WelcomeNewPlayer) action).getSender().getId() + " NEW PLAYER: "
						+ ((WelcomeNewPlayer) action).getNewPlayer().getId() + " #####");
			}
			if (!(action instanceof PassToken)) {
				System.out.println("##BUFFER### WAiting buffercontroller to be free ");
			}

			while (!bufferController.imFree) {
				
			}

			if (!(action instanceof PassToken)) {
				System.out.println("##BUFFER### buffercontroller is free ");
			}

			
			
					if (action instanceof PassToken) {
						
						bufferController.receivedToken();
						return true;

					}
					
					synchronized (bufferController) {
					synchronized (actions) {
					actions.add(action);
					System.out.println("##BUFFER### ADDED ACTION IN ACTIONS " + action.getClass()
							+ " TITAL NUMBER IN ACTIONS: " + actions.size() + "#####");

					bufferController.notify();
					return true;
				}
			}

		}

		else {

			synchronized (actionsThatNeedsAToken) {

				actionsThatNeedsAToken.add(action);
				System.out.println("##BUFFER### ADDED ON BUFFER: " + action.getClass() + " #####");

			}

		}
		return true;

	}

	public static Action getFirstActionThatNeedAToken() {
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

	public static Action getFirstAction() {
		synchronized (actions) {
			if (!actions.isEmpty()) {
				Action action = new Action();
				synchronized (actions) {
					action = actions.get(0);
					actions.remove(0);
				}
				return action;

			}
		}

		return null;
	}

	public static ArrayList<Action> getAllActions() {
		synchronized (actions) {
			return actions;
		}
	}

	public static ArrayList<Action> getAllActionsThatNeedsAToken() {
		synchronized (actionsThatNeedsAToken) {

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
