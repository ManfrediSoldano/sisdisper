package sisdisper.server.model.comunication;

import javax.xml.bind.annotation.XmlRootElement;

import sisdisper.server.model.Game;
import sisdisper.server.model.Player;

@XmlRootElement
public class AddToGame {
	private Game game = new Game();
	private Player player = new Player();

	public AddToGame(){
		
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
