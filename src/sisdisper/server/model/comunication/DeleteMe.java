package sisdisper.server.model.comunication;

import sisdisper.server.model.Game;
import sisdisper.server.model.Player;

public class DeleteMe {
	private Game game = new Game();
	private Player player = new Player();

	public DeleteMe(){
		
	}

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
