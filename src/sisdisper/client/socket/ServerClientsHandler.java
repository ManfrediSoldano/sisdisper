package sisdisper.client.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.Action;

public class ServerClientsHandler extends Thread {
	 private Socket socket;
     
     PrintWriter out;
     Scanner in;
     InetAddress address;
     Buffer buffer;
     
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
      * Services this thread's client by first sending the
      * client a welcome message then repeatedly reading strings
      * and sending back the capitalized version of the string.
      */
     
     public void run() {
         
    	 
    	  try {
             in = new Scanner(socket.getInputStream());
			 out = new PrintWriter(socket.getOutputStream(), true);
			 
			 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        
    	  
    	  while(true){
    		  try {
    				String whil = in.nextLine();
    				
    				
    				setReceived_text(whil);
    			} catch (Exception exc) {
    				
    			}
    	  }
             

     }

     public void setReceived_text(String received_text) throws JAXBException {
 		JAXBContext jaxbContext = JAXBContext.newInstance(Action.class);
 		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
 		StringReader reader = new StringReader(received_text);
 		Action action = (Action) jaxbUnmarshaller.unmarshal(reader);
 		
 		buffer.addAction(action);
 		
 	}
     
     
     public void sendMessage(String Message){
    	 out.println(Message);
     }
}

/* *        
 *   } catch (IOException e) {
             log("Error handling client# " + clientNumber + ": " + e);
         } finally {
             try {
                 socket.close();
             } catch (IOException e) {
                 log("Couldn't close a socket, what's going on?");
             }
             log("Connection with client# " + clientNumber + " closed");
         }  
         
         **/
