package sisdisper.client.model.action;

import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
import sisdisper.server.model.Game;

public class CreateGame extends Action {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	private Game game;

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Boolean execute() {

		ClientToServerCommunication com = new ClientToServerCommunication();

		if (BufferController.mygame == null) {
			if (!BufferController.me.getId().equals(null)) {
				game.addPlayer(BufferController.me);
				String returnString = com.createNewGame(game);
				if (returnString.equals("ack")) {
					BufferController.mygame = game;
					BufferController.cli.returnCreated("Game correctly created", false, true);
					BufferController.next = BufferController.me;
					BufferController.prev = BufferController.me;
				}

				else {
					BufferController.cli.returnCreated(returnString, true, false);
				}
				synchronized (BufferController.cli) {
					BufferController.cli.notify();
				}
			}
		} else {
			BufferController.cli.returnCreated("WTF_Error#1: You are already inside a game! ", false, false);
			synchronized (BufferController.cli) {
				BufferController.cli.notify();
			}
		}

		return true;
	}

}
