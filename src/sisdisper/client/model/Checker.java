package sisdisper.client.model;

import java.util.ArrayList;

import sisdisper.client.model.action.Action;

public class Checker {
	private static Buffer instance = null;

	public static Buffer getIstance() {
		if (instance == null) {
			instance = new Buffer();
		}
		return instance;
	}

	private static ArrayList<Action> actions = new ArrayList<Action>();
	private static ArrayList<Action> actionsThatNeedsAToken = new ArrayList<Action>();

	public Checker() {
		getIstance();
	}



	public Action getFirstAction() {
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

	public void addAction(Action action) {
		synchronized (actions) {
			actions.add(action);
		}
	}
	
	public void addActionThatNeedsAToken(Action action) {
		synchronized (actionsThatNeedsAToken) {
			actionsThatNeedsAToken.add(action);
		}
	}

	public synchronized ArrayList<Action> getAllActions() {

		return actions;
	}

}
