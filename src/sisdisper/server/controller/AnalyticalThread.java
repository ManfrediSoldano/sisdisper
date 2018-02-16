package sisdisper.server.controller;

public class AnalyticalThread implements Runnable {
	private Thread t;
	
	public void start() {
		t = new Thread(this);
		t.start();
	}
	
	@Override
	public void run() {
		
	}

}
