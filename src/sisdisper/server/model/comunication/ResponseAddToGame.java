package sisdisper.server.model.comunication;

import javax.xml.bind.annotation.XmlRootElement;

import sisdisper.server.model.Game;
@XmlRootElement
public class ResponseAddToGame {
	private Game game = new Game();
	
	public enum Type {
		ACK,
		AREADY_EXIST,
		GAME_NOT_FOUND		
	}
	
	private Type type;
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
}
