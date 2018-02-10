package sisdisper.client.model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;



import sisdisper.client.BufferController;
import sisdisper.client.model.CountingSemaphore;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AddBomb;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.ExplodingBomb;
import sisdisper.client.model.action.MoveCLI;
import sisdisper.client.model.action.NewPlayer;
import sisdisper.client.model.action.NewPlayerResponse;


public class Buffer implements Observer{
	private static BufferController bufferController;
	private static Buffer instance = null;
	private CountingSemaphore semaphore = CountingSemaphore.getInstance();

	public static Buffer getIstance() {
		if (instance == null) {
			instance = new Buffer();
		}
		return instance;
	}

	private static ArrayList<Action> actions = new ArrayList<Action>();
	public static ArrayList<Action> actionsThatNeedsAToken = new ArrayList<Action>();

    public void update(Observable obj, Object arg) {
    	Action action = (Action)arg;
    	System.out.println("Inside the update function of buffer"+obj+arg);
    	
    	if (!(action instanceof MoveCLI) && !(action instanceof Bomb) && !(action instanceof NewPlayer)	&& !(action instanceof AddBomb) && !(action instanceof ExplodingBomb) && !(action instanceof NewPlayerResponse)) {
    		
    		synchronized (actions) {
    			System.out.println("Action:" +action);
				actions.add(action);
				
				semaphore.take();
			}
    		
    	} else {
    		synchronized (actionsThatNeedsAToken) {
    			actionsThatNeedsAToken.add(action);
    		}
    	}
    	
    	
    }
	

	public static Action getFirstActionThatNeedAToken() {
		synchronized (actionsThatNeedsAToken) {
			if (!actionsThatNeedsAToken.isEmpty()) {

				Action action = actionsThatNeedsAToken.get(0);
				if (!(action instanceof NewPlayer) && !(action instanceof NewPlayerResponse) && !(action instanceof ExplodingBomb)) {
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
