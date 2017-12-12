package sisdisper.client.model;

public class CountingSemaphore {
	  private int signals = 0;

	  private static CountingSemaphore instance = null;
	  
	   protected CountingSemaphore() {
	      // Exists only to defeat instantiation.
	   }
	   public static CountingSemaphore getInstance() {
	      if(instance == null) {
	         instance = new CountingSemaphore();
	      }
	      return instance;
	   }
	  
	  public synchronized void take() {
	    this.signals++;
	    this.notify();
	  }

	  public synchronized void release() throws InterruptedException{
	    while(this.signals == 0) wait();
	    this.signals--;
	  }

	}