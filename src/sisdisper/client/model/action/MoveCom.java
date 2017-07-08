package sisdisper.client.model.action;

import sisdisper.client.socket.Client;
import sisdisper.server.model.Coordinate;
import sisdisper.server.model.Player;

public class MoveCom extends Action {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

Coordinate coordinate;
Client client;
Player player;

public Player getPlayer() {
	return player;
}

public void setPlayer(Player player) {
	this.player = player;
}

public Coordinate getCoordinate() {
	return coordinate;
}

public void setCoordinate(Coordinate coordinate) {
	this.coordinate = coordinate;
}

}
