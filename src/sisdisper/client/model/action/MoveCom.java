package sisdisper.client.model.action;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
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

	public Boolean execute() {
		ClientToServerCommunication com = new ClientToServerCommunication();
		
		BufferController.cli.publishString("RECEIVED A MOVE REQUEST FROM " + player.getId() );
		
		if (coordinate.equal(BufferController.me.getCoordinate())) {

			ResponseMove response = new ResponseMove();
			response.setPlayer(BufferController.me);
			response.setNext(BufferController.next);
			response.setPrev(BufferController.prev);
			
			BufferController.cli.publishString("My next: " + BufferController.next + " my prev: " + BufferController.prev );
			response.setResponse(ResponseMove.Response.KILLED_ME);

			BufferController.cli.publishString("Killed by" + player.getId());

			try {
				BufferController.server.sendMessageToPlayer(player, response);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			com.deleteMe(BufferController.me.getId(), BufferController.mygame.getId(),Integer.toString(BufferController.me.getPoint()),"loser");
			
			BufferController.end = true;
			BufferController.alive=false;


		} else {
			ResponseMove response = new ResponseMove();
			response.setPlayer(BufferController.me);
			response.setResponse(ResponseMove.Response.ACK);
			try {
				BufferController.server.sendMessageToPlayer(player, response);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
