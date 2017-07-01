package sisdisper.client.model.action;

import sisdisper.server.model.Player;

public class NewPlayer  implements Action  {
 private Player player;

public Player getPlayer() {
	return player;
}

public void setPlayer(Player player) {
	this.player = player;
}
}
