package sisdisper.client.model;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.xml.bind.JAXBException;

import sisdisper.client.BufferController;
import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AskPosition;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.MoveCLI;
import sisdisper.client.model.action.MoveCom;
import sisdisper.client.model.action.NewPlayer;
import sisdisper.client.model.action.WelcomeNewPlayer;
import sisdisper.client.socket.Client;
import sisdisper.server.controller.RestServer;

public class Buffer {
	private BufferController bufferController;
	private static Buffer instance = null;

	public static Buffer getIstance() {
		if (instance == null) {
			instance = new Buffer();
		}
		return instance;
	}

	ArrayList<Action> actions = new ArrayList<Action>();
	ArrayList<Action> actionsThatNeedsAToken = new ArrayList<Action>();

	public synchronized Boolean addAction(Action action) {

		if (!(action instanceof MoveCLI) && !(action instanceof Bomb)) {
			actions.add(action);
			synchronized (bufferController) {
				bufferController.notify();
			}
			return true;
		} else {

			actionsThatNeedsAToken.add(action);
			return true;
		}
	}

	public synchronized Boolean addAction(Action action, Client client) throws JAXBException, InterruptedException {
		if (action instanceof WelcomeNewPlayer) {
			for (Action deleteAction : actions) {
				if (deleteAction instanceof NewPlayer) {
					if (((NewPlayer) deleteAction).getPlayer() == ((WelcomeNewPlayer) action).getNewPlayer()) {
						actions.remove(deleteAction);
						Ack ack = new Ack();
						ack.setPlayer(((WelcomeNewPlayer) action).getNewPlayer());

						client.send(ack);
						wait();

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
		
		
		actions.add(action);

		synchronized (bufferController) {
			bufferController.notify();
		}
		return true;

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
			Action action = actions.get(0);
			actions.remove(0);
			return action;

		}

		return null;
	}

	public synchronized ArrayList<Action> getAllActions() {
		return actions;
	}

	public BufferController getBufferController() {
		return bufferController;
	}

	public void setBufferController(BufferController bufferController) {
		this.bufferController = bufferController;
	}

}
