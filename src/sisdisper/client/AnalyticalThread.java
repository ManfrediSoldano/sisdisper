package sisdisper.client;


public class AnalyticalThread implements Runnable {
	private Thread t;
	public boolean end = true;
	public String ID;
	public void start() {
		t = new Thread(this);
		t.start();
	}
	
	@Override
	public void run() {
		
		while(end) {
			ClientToServerCommunication com = new ClientToServerCommunication();
			String game = com.getLiveAnalytics(ID);
			System.out.println(game);
		}
	}

}
