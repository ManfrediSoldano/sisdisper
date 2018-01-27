package sisdisper.client;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.CountingSemaphore;
import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.AckAfterBomb;
import sisdisper.client.model.action.AckNewPlayerAdded;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AddBomb;
import sisdisper.client.model.action.AddMeToGame;
import sisdisper.client.model.action.AddMeToYourClients;
import sisdisper.client.model.action.AddMeToYourClients_NotPassToBuffer;
import sisdisper.client.model.action.AdviceBomb;
import sisdisper.client.model.action.AfterBombCheck;
import sisdisper.client.model.action.AskPosition;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.CLINewPlayer;
import sisdisper.client.model.action.CreateGame;
import sisdisper.client.model.action.DeleteMe;
import sisdisper.client.model.action.Deleted;
import sisdisper.client.model.action.ExplodingBomb;
import sisdisper.client.model.action.GetGamesFromServer;
import sisdisper.client.model.action.MoveCLI;
import sisdisper.client.model.action.MoveCom;
import sisdisper.client.model.action.NewPlayer;
import sisdisper.client.model.action.NewPlayerResponse;
import sisdisper.client.model.action.PassToken;
import sisdisper.client.model.action.PlayerReceivedAPoint;
import sisdisper.client.model.action.ResponseMove;
import sisdisper.client.model.action.ReturnPosition;
import sisdisper.client.model.action.UpdateYourNextPrev;
import sisdisper.client.model.action.WelcomeNewPlayer;
import sisdisper.client.model.action.Winner;
import sisdisper.client.socket.Client;
import sisdisper.client.socket.Server;
import sisdisper.client.view.CLI;
import sisdisper.client.view.UserObservable;
import sisdisper.server.model.Coordinate;
import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;
import sisdisper.server.model.comunication.ResponseAddToGame.Type;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public class BufferController implements Runnable {
		
	
	
	public Boolean imFree = true;
	private Boolean test_something_changed = false;
	public String test = "";
	private int test_count =0;
	
	
	//used by actions
	public static CLI cli;
	public static Game mygame=null;
	public static Player me = new Player();
	public static Server server = new Server();
	public static Player next = new Player();
	public static Player prev = new Player();
	public static ArrayList<Client> clients = new ArrayList<Client>();
	public static int numberAck = 0;
	public static Boolean tokenBlocker = false;
	public static ArrayList<Coordinate> receivedCoordinate = new ArrayList<Coordinate>();
	public static Boolean end = false;
	public static ArrayList<ResponseMove> responseMoves = new ArrayList<ResponseMove>();
    public static int points = 0;
    public static int winpoint = 3;
	public static ArrayList<AckAfterBomb> ack = new ArrayList<AckAfterBomb>();
	public static UpdateYourNextPrev tokenUpdate = new UpdateYourNextPrev();
	public static ArrayList<Deleted> deleted = new ArrayList<Deleted>();
	public static Boolean addingAPlayer = false;

	//Da capire cosa faccia
	public static Boolean block = false;
	
	//internals
	//Semaphore
	private CountingSemaphore semaphore = CountingSemaphore.getInstance();
	private Thread t;
	public static Buffer buffer;

	public void start() {
		t = new Thread(this);
		t.start();

		buffer = new Buffer();
		buffer.setBufferController(this);

		cli = new CLI();
		cli.setBuffer(buffer);
		UserObservable observable = new UserObservable();
		cli.setObservable(observable);
		observable.addObserver(buffer);
		cli.start();

	}

	public Player getMe() {
		return me;
	}

	@Override
	public void run() {
		
		Action action = new Action();
		// Add me on a game

		while (!end) {

			
			try{
			semaphore.release();
			System.out.println("Befor getting the action");
			action = Buffer.getFirstAction();
			System.out.println("After getting the action");
			action.execute();

			}catch(Exception e){
				
			}
			

		}

	}




}
