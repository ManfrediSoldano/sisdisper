package sisdisper.client.model.action;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.socket.Client;
import sisdisper.server.model.Coordinate;
import sisdisper.server.model.Player;

@XmlRootElement(name = "AskPosition")
public class AskPosition extends Action {
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
		BufferController.cli.publishString("###BUFFERController## POSITION REQUESTED BY "+ player.getId() + " #####");
		ReturnPosition rtn = new ReturnPosition();
		rtn.setCoordinate(BufferController.me.getCoordinate());
		try {
			BufferController.server.sendMessageToPlayer(player, rtn);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return true;
	}

}
