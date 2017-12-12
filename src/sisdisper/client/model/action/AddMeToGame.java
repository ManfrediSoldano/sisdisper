package sisdisper.client.model.action;

import sisdisper.server.model.Game;
import sisdisper.server.model.Player;

public class AddMeToGame  extends Action  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	public Boolean execute(Game game){
		return true;
	}
	
}
