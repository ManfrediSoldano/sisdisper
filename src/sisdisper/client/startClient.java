package sisdisper.client;

import sisdisper.client.model.Buffer;

public class startClient {

	
	public static void main(String[] args) {
		BufferController buffercontroller = new BufferController();
		Buffer buffer = new Buffer();
		
		buffercontroller.start();
		
		
	}


}
