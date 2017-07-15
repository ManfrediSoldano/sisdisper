package sisdisper.client.socket;

import java.io.IOException;

import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AddMeToYourClients;
import sisdisper.client.model.action.AddMeToYourClients_NotPassToBuffer;
import sisdisper.client.model.action.NewPlayer;
import sisdisper.client.model.action.PassToken;

public class ServerClientsHandler extends Thread {
	private Socket socket;

	PrintWriter out;
	Scanner in;
	InetAddress address;
	Buffer buffer;
	private Thread t;
	String player_id = "";

	public String getPlayer_id() {
		return player_id;
	}

	public void setPlayer_id(String id) {
		this.player_id = id;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(int clientNumber) {
		this.clientNumber = clientNumber;
	}

	int clientNumber;

	public ServerClientsHandler(Socket socket) {
		this.socket = socket;
		this.address = socket.getInetAddress();
		this.clientNumber = socket.getPort();
	}

	/**
	 * Services this thread's client by first sending the client a welcome
	 * message then repeatedly reading strings and sending back the capitalized
	 * version of the string.
	 */

	public void start() {
		t = new Thread(this);
		t.start();
		buffer = new Buffer();

	}

	public void run() {
		try {
			try {
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (true) {
				try {
					String whil = in.nextLine();

					setReceived_text(whil);
				} catch (Exception exc) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void setReceived_text(String received_text) {

		ObjectMapper mapper = new ObjectMapper();

		try {
			String saction = mapper.readValue(received_text, String.class);
			Action deser = new Action();
			Action action = deser.deserialize(saction);
			if (action instanceof NewPlayer) {
				System.out.println("@@@SERVERClientHandler@@@ NewPlayer received from: "
						+ ((NewPlayer) action).getPlayer().getId() + " @@@@@ ");
				player_id = ((NewPlayer) action).getPlayer().getId();
			}

			if (action instanceof AddMeToYourClients) {
				System.out.println("@@@SERVERClientHandler@@@ AddMeToYourClients received from: "
						+ ((AddMeToYourClients) action).getPlayer().getId() + " @@@@@ ");
				player_id = ((AddMeToYourClients) action).getPlayer().getId();
			}
			if (action instanceof AddMeToYourClients_NotPassToBuffer) {
				System.out.println("@@@SERVERClientHandler@@@ AddMeToYourClients_NotPassToBuffer received from: "
						+ ((AddMeToYourClients_NotPassToBuffer) action).getPlayer().getId() + " @@@@@ ");
				player_id = ((AddMeToYourClients_NotPassToBuffer) action).getPlayer().getId();
			}

			if (!(action instanceof AddMeToYourClients_NotPassToBuffer)) {
				Buffer.addAction(action);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendMessage(String Message) {
		// System.out.println("@@@SERVERClientHandler@@@ SENDING "+Message+"
		// @@@@@ ");

		out.println(Message);
	}
}

/*
 * * } catch (IOException e) { log("Error handling client# " + clientNumber +
 * ": " + e); } finally { try { socket.close(); } catch (IOException e) {
 * log("Couldn't close a socket, what's going on?"); }
 * log("Connection with client# " + clientNumber + " closed"); }
 * 
 **/
