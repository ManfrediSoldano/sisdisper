package sisdisper.client.model.action;

import sisdisper.client.BufferController;
import sisdisper.server.model.Player;

public class PlayerReceivedAPoint extends Action {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	Player player;

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	int points;

	public Boolean execute() {
		BufferController.cli.returnMove("Player " + player.getId()
				+ " has just gained a point, now he have " + getPoints()
				+ " points");
		BufferController.cli.returnMove("You have " + BufferController.points + " points");
		return true;
	}

}
