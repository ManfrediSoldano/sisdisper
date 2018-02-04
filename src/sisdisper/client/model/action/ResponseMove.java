package sisdisper.client.model.action;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
import sisdisper.client.socket.Client;
import sisdisper.server.model.Player;

public class ResponseMove extends Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Response {
		ACK, KILLED_ME, ERROR
	}

	private Player player;
	private Player next;
	private Player prev;

	public Player getNext() {
		return next;
	}

	public void setNext(Player next) {
		this.next = next;
	}

	public Player getPrev() {
		return prev;
	}

	public void setPrev(Player prev) {
		this.prev = prev;
	}

	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Boolean execute() {
		BufferController.cli.publishString("RECEIVED RESPONSE FROM "	+ player.getId() + " #### ");
		Boolean killed = false;
		BufferController.responseMoves.add(this);

		if (BufferController.responseMoves.size() == BufferController.mygame.getPlayerList().size() - 1) {
			for (ResponseMove responseMove : BufferController.responseMoves) {

				if (responseMove.getResponse() == ResponseMove.Response.KILLED_ME) {
					BufferController.points++;

					BufferController.cli.returnMove("Killed: " + responseMove.getPlayer().getId());
					PlayerReceivedAPoint point = new PlayerReceivedAPoint();
					point.setPlayer(BufferController.me);
					point.setPoints(BufferController.points);

					try {
						BufferController.server.sendMessageToAll(point);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}

					if (BufferController.points >= BufferController.winpoint) {
						BufferController.cli.returnMove("YOU'RE THE WINNER!");
						Winner winner = new Winner();
						winner.setPlayer(BufferController.me);
						try {
							BufferController.server.sendMessageToAll(winner);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
						ClientToServerCommunication com = new ClientToServerCommunication();

						com.deleteMe(BufferController.me.getId(), BufferController.mygame.getId());
						for (Client client : BufferController.clients) {
							client.end = true;
						}
					}

					killed = true;
					BufferController.tokenBlocker = true;

					BufferController.mygame.removePlayer(responseMove.getPlayer().getId());

					if (BufferController.mygame.getPlayerList().size() != 1) {
						DeleteMe deleteme = new DeleteMe();
						deleteme.setPlayer(player);
						deleteme.setNext(next);
						deleteme.setPrev(prev);
						deleteme.setSender(BufferController.me);

						if (next.getId().equals(BufferController.me.getId())) {
							BufferController.prev = prev;
							BufferController.cli.publishString("####BUFFERController## NEW PREV: " + prev.getId() + " ####");

						}
						if (prev.getId().equals(BufferController.me.getId())) {
							BufferController.next = next;
							BufferController.cli.publishString("####BUFFERController## NEW NEXT: " + next.getId() + " ####");

						}

						try {
							BufferController.server.sendMessageToAll(deleteme);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
					} else {
						killed = false;
						BufferController.next = BufferController.me;
						BufferController.prev = BufferController.me;
					}

				}
			}

			BufferController.cli.returnMove("New position: X: " + BufferController.me.getCoordinate().getX() + "Y:" + BufferController.me.getCoordinate().getY());
			BufferController.cli.returnMove("Now you have an amount of: " + BufferController.points);
			BufferController.cli.returnMove("Zone: " + BufferController.me.getArea(BufferController.mygame.getDimension()));
			BufferController.responseMoves = new ArrayList<ResponseMove>();
			
			BufferController.cli.move(BufferController.me.getCoordinate().getX(),BufferController.me.getCoordinate().getY());
			synchronized (BufferController.cli) {
				BufferController.cli.notify();
			}
			
			if (!killed) {
				BufferController.tokenBlocker = false;
				BufferController.cli.returnMove("Move completed");
				PassToken token = new PassToken();
				token.execute();
			}

		}

		return true;
	}

}
