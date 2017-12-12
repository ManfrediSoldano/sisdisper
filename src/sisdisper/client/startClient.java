package sisdisper.client;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.CountingSemaphore;


public class startClient {

	
	public static void main(String[] args) {
		
		
		BufferController buffercontroller = new BufferController();
		buffercontroller.start();
		
		Buffer buffer = new Buffer();
		AccelerometerManager acc = new AccelerometerManager();
		BombObservable bomb = new BombObservable();
		acc.setObservable(bomb);
		bomb.addObserver(buffer);
		acc.start();
		
		buffercontroller.start();
		
	}


}
