package sisdisper.client.model.action;

import sisdisper.client.BufferController;
import sisdisper.server.model.Area;
import sisdisper.server.model.Player;

public class AdviceBomb extends Action {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	/**
		 * 
		 */
	public Area area;
	public Player player;

	public Boolean execute() {
		BufferController.cli.returnBomb("In 5 second one bomb sent by " + player.getId()
				+ " will explode in the " + area + " area.");
		BufferController.cli.returnBomb("You're currently in the " + BufferController.me.getArea(BufferController.mygame.getDimension()) + " area");
		return true;
	}
}
