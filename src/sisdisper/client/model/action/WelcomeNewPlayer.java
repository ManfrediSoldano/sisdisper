package sisdisper.client.model.action;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.model.Buffer;
import sisdisper.client.socket.Client;
import sisdisper.client.socket.ClientObservable;
import sisdisper.server.model.Player;

public class WelcomeNewPlayer extends Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Player next = new Player();
	Player prev = new Player();
	Player newPlayer = new Player();
	Player sender = new Player();

	public Player getSender() {
		return sender;
	}

	public void setSender(Player sender) {
		this.sender = sender;
	}

	public Player getNewPlayer() {
		return newPlayer;
	}

	public void setNewPlayer(Player newPlayer) {
		this.newPlayer = newPlayer;
	}

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

	public Boolean execute() {
		System.out.println("###WelcomeNewPLayer## WELCOME NEW PLAYER #####");
		cancelNewPlayer();
		
		// Caso in cui il nuovo aggiunto non sono io
		if (!newPlayer.getId().equals(BufferController.me.getId())) {

			System.out.println("##WelcomeNewPlayer### RECEIVED A NEW ONE #####");
			// Lo agigungo ai miei client

			Client client = new Client(newPlayer);
			ClientObservable observ = new ClientObservable();
			client.setClientObserver(observ);
			observ.addObserver(BufferController.buffer);
			client.start();
			// Faccio sì che il nuovo server abbia in mano il mio nome
			AddMeToYourClients_NotPassToBuffer addMeToYourClients = new AddMeToYourClients_NotPassToBuffer();
			addMeToYourClients.setPlayer(BufferController.me);

			try {
				synchronized (client) {
					client.send(addMeToYourClients);
				}
			} catch (JsonProcessingException e1) {

				e1.printStackTrace();
			}
			// Aggiungo il nuovo clients tra i miei
			BufferController.clients.add(client);
			// Aggiungo il giocatore nella mia lista
			ArrayList<Player> ply = BufferController.mygame.getPlayerList();
			ply.add(newPlayer);
			BufferController.mygame.setPlayerList(ply);

			// Se il giocatore non deve cambiare il next
			if (!sender.getId().equals(BufferController.next.getId())) {
				System.out.println("##WelcomeNewPlayer### SENDING AN ACK TO: " + next.getId() + " #####");

				try {
					// Invia un AckNewPlayerAdded al nuovo giocatore indicando
					// che ha fatto tutto
					AckNewPlayerAdded ack = new AckNewPlayerAdded();
					ack.setPlayer(newPlayer);
					ack.setSender(BufferController.me);
					BufferController.server.sendMessageToPlayer(newPlayer, ack);

				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

		} else {
			// Vuol dire che io sono stato aggiunto alla partita
			System.out.println("##WelcomeNewPlayer### I WAS ADDED #####");
			// Imposto i next e prev come indicato dal token peer
			BufferController.next = this.next;
			BufferController.prev = this.prev;

			if (BufferController.next.getId().equals(BufferController.prev.getId())) {
				// Siamo solo in due nella partita, quindi non ha senso
				// aspettarsi altri ack
				// Avviso il token-peer che può continuare a lavorare

				try {
					System.out.println("##WelcomeNewPlayer### SENDING ALLACKCHECKED TO TOKEN PEER -- ONLY ME AND TOKEN PEER ARE CURRENTLY IN THE GAME #####");
					BufferController.server.sendMessageToPlayer(BufferController.next, new AckAllPlayerAddedTheNewOne());

				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			
			if(BufferController.ackProcessedBeforeWelcomeNewPlayer) {
				System.out.println("##WelcomeNewPlayer### SENDING ALLACKCHECKED TO TOKEN PEER -- All Ack were elaborated before i reached the welcome new player#####");
				try {
					BufferController.server.sendMessageToPlayer(BufferController.next, new AckAllPlayerAddedTheNewOne());
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				BufferController.ackProcessedBeforeWelcomeNewPlayer=false;
			}
		}

		// Se devo cambiare la mia formuazione:
		if (sender.getId().equals(BufferController.next.getId())
				&& !newPlayer.getId().equals(BufferController.me.getId())) {
			System.out.println("##WelcomeNewPlayer### I'M THE NEXT ONE #####");
			BufferController.next = this.next;
			try {
				AckNewPlayerAdded ack = new AckNewPlayerAdded();
				ack.setPlayer(newPlayer);
				BufferController.server.sendMessageToPlayer(newPlayer, ack);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		}
		return true;
	}

	public void cancelNewPlayer() {
		ArrayList<Action> actions = Buffer.getAllActionsThatNeedsAToken();
		Action actiotodelete = null;
		for (Action action : actions) {
			if (action instanceof NewPlayer) {
				if (((NewPlayer) action).getPlayer().getId().equals(newPlayer.getId())) {
					actiotodelete = action;
				}
			}
		}
		actions = null;
		if (actiotodelete != null) {
			Buffer.deleteAction(actiotodelete);
		} else {
			BufferController.cli.publishString(
					"###WelcomeNewPlayer# Something strange happened when i tried to delete a New player##");
			BufferController.cli
					.publishString("###WelcomeNewPlayer# Didn't find the newPLayer but i'm in  welcomenew player##");
		}
	}
}
