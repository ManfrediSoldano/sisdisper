package sisdisper.client;

public class startClient {

	public static void main(String[] args)  {
		ClientToServerCommunication com = new ClientToServerCommunication();
		com.getGamesFromServer();
	}
	
}
