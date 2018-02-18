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
		ClientToServerCommunication com = new ClientToServerCommunication();

		while(end) {
			try {
			String game = com.getLiveAnalytics(ID);
			System.out.println(game);

			}catch(Exception e){
				//One Exception to rule them all, One Exception to find them,
				//One Exception to bring them all and in the darkness bind them.
				
				//I don't need it anyway
			}
		}
	}

}
