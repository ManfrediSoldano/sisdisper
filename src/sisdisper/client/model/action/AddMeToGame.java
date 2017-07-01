package sisdisper.client.model.action;

import sisdisper.server.model.Game;
import sisdisper.server.model.Player;

public class AddMeToGame  implements Action  {
	Player player;
	Game game;
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
