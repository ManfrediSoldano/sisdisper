package sisdisper.client.model.action;

import sisdisper.client.socket.Client;
import sisdisper.server.model.Coordinate;
import sisdisper.server.model.Player;

public class AskPosition implements Action {
 Coordinate coordinate = new Coordinate();
 Client client;
 Player player;

public Player getPlayer() {
	return player;
}

public void setPlayer(Player player) {
	this.player = player;
}

public Client getClient() {
	return client;
}

public void setClient(Client client) {
	this.client = client;
}

public Coordinate getCoordinate() {
	return coordinate;
}

public void setCoordinate(Coordinate coordinate) {
	this.coordinate = coordinate;
}
 
 
}
