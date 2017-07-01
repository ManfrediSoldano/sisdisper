package sisdisper.client.model.action;

import sisdisper.client.socket.Client;
import sisdisper.server.model.Coordinate;

public class MoveCom implements Action {
public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

Coordinate coordinate;
Client client;

public Coordinate getCoordinate() {
	return coordinate;
}

public void setCoordinate(Coordinate coordinate) {
	this.coordinate = coordinate;
}

}
