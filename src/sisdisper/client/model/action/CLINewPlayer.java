package sisdisper.client.model.action;

import sisdisper.client.BufferController;
import sisdisper.server.model.Player;

public class CLINewPlayer extends Action {
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
		BufferController.me = player;
		BufferController.server.setPlayer(BufferController.me);
		BufferController.server.start();
		synchronized (BufferController.cli) {
			BufferController.cli.notify();
		}
		return true;
	}
}
