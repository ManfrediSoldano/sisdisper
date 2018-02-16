package sisdisper.client.model.action;

import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
import sisdisper.client.socket.Client;
import sisdisper.server.model.Player;

public class Winner extends Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Player player;

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Boolean execute() {
		ClientToServerCommunication com = new ClientToServerCommunication();

		BufferController.cli.returnMove(player.getId() + " HAS WON THIS MATCH!");
		com.deleteMe(BufferController.me.getId(), BufferController.mygame.getId(), Integer.toString(BufferController.me.getPoint()),"loser");
		for (Client client : BufferController.clients) {
			client.end = true;
		}
		BufferController.alive=false;
		return true;
	}

}
