package sisdisper.client;

import sisdisper.client.model.Buffer;

public class startClient {

	
	public static void main(String[] args) {
		BufferController buffercontroller = new BufferController();
		buffercontroller.start();
		Buffer buffer = new Buffer();
		AccelerometerManager acc = new AccelerometerManager();
		acc.start();
		buffercontroller.start();
		
	}


}
