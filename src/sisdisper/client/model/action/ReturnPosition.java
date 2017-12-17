package sisdisper.client.model.action;

import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.socket.Client;
import sisdisper.server.model.Coordinate;
import sisdisper.server.model.Player;

public class ReturnPosition extends Action{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	public Boolean execute() {
		BufferController.receivedCoordinate.add(coordinate);
		if (BufferController.receivedCoordinate.size() == BufferController.mygame.getPlayerList().size() - 1) {
			Boolean ok = true;
			while (ok) {

				int x = ThreadLocalRandom.current().nextInt(0, BufferController.mygame.getDimension());
				int y = ThreadLocalRandom.current().nextInt(0, BufferController.mygame.getDimension());
				
				for (Coordinate coordinata : BufferController.receivedCoordinate) {
					if (coordinata != null) {
						if (coordinata.getX() == x && coordinata.getY() == y) {
							System.out.println("###BUFFERController## POSIZIONE GIA' OCCUPATA #####");
							ok = false;
						}
					}
				}
				
				if (ok) {
					ok = false;
					Coordinate coordinata_player = new Coordinate();
					coordinata_player.setX(x);
					coordinata_player.setY(y);
					BufferController.me.setCoordinate(coordinata_player);
					BufferController.tokenBlocker = false;
					BufferController.cli.publishString("New position--> x: " + BufferController.me.getCoordinate().getX() + " and y: "
							+ BufferController.me.getCoordinate().getY());
					BufferController.cli.returnMove("Zone: " + BufferController.me.getArea(BufferController.mygame.getDimension()));
					PassToken token = new PassToken();
					token.execute();

				} else {
					ok = true;
				}
			}

		}

		return true;
	}
	
}
