package sisdisper.client.model.action;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
import sisdisper.client.socket.Client;
import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.ResponseAddToGame;

public class AddMeToGame  extends Action  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Player player;
	Game game;
	
	
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
	
	public Boolean execute(){

		if (BufferController.mygame==null) {
			ClientToServerCommunication com = new ClientToServerCommunication();

			if (!BufferController.me.getId().equals(null)) {
				AddToGame addme = new AddToGame();
				addme.setPlayer(BufferController.me);
				addme.setGame(game);
				ResponseAddToGame response = com.putMeOnaGame(addme);
				ResponseAddToGame.Type type = response.getType();

				if (type == ResponseAddToGame.Type.ACK) {
					BufferController.mygame = ((ResponseAddToGame) response).getGame();
					BufferController.cli.returnAdded((ResponseAddToGame) response);
					synchronized (BufferController.cli) {
						BufferController.cli.notify();
					}
					BufferController.next = BufferController.me;
					BufferController.prev = BufferController.me;
					//DA FARE
					adviceOfMyPresence();

				
				}
				if (type != ResponseAddToGame.Type.ACK) {
					BufferController.cli.returnAdded((ResponseAddToGame) response);
					synchronized (BufferController.cli) {
						BufferController.cli.notify();
					}
				}
			} else {
				//// AGGIUNGERE RITORNO ERRORE////
			}
		} else {
			ResponseAddToGame respone = new ResponseAddToGame();
			respone.setType(ResponseAddToGame.Type.AREADY_EXIST);
			BufferController.cli.returnAdded(respone);
			synchronized (BufferController.cli) {
				BufferController.cli.notifyAll();
			}
		}
		return true;
	}
	
	private void adviceOfMyPresence() {
		// Check per verificare che tutti mi abbiano dato l'ok prima di andare
		// avanti
		for (Player player : BufferController.mygame.getPlayerList()) {
			if (!player.getId().equals(BufferController.me.getId())) {
				Client client = new Client(player);
				BufferController.clients.add(client);
				client.start();
			}
		}
		for (Client client : BufferController.clients) {
			NewPlayer newp = new NewPlayer();
			newp.setPlayer(BufferController.me);
			if (client != null) {
				try {
					client.send(newp);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
}
