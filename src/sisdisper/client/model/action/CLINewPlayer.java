package sisdisper.client.model.action;

import sisdisper.server.model.Player;

public class CLINewPlayer  extends Action  {
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
}
