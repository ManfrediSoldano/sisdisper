package sisdisper.client;

import java.util.ArrayList;

import sisdisper.client.accelerometer.AccelerometerSimulator;
import sisdisper.client.accelerometer.Measurement;
import sisdisper.client.accelerometer.SensorBuffer;
import sisdisper.client.accelerometer.Simulator;
import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.AddBomb;
import sisdisper.server.model.Area;

public class AccelerometerManager implements Runnable {
	public SensorBuffer sensorbuffer = new SensorBuffer();
	private Thread t;
	@SuppressWarnings("unused")
	private Buffer buffer;
	private Simulator simulator;
	private ArrayList<Measurement> measure = new ArrayList<Measurement>();
	private Double emai = 60.0;
	private BombObservable observable = null;
	@SuppressWarnings("unchecked")
	public void start() {
		t = new Thread(this);
		t.start();

		buffer = new Buffer();
		

	}

	public BombObservable getObservable() {
		return observable;
	}

	public void setObservable(BombObservable observable) {
		this.observable = observable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		simulator = new AccelerometerSimulator(sensorbuffer);
		simulator.run();
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			measure = sensorbuffer.readAllAndClean();
			if (measure.size() != 0) {
				//System.out.println("Measure size" + measure.size());
				double sum = 0;
				int number = 0;
				for (Measurement measur : measure) {
					sum += measur.getValue();
					number++;
				}

				double avg = sum / number;
				double actualema = 0;
				actualema = emai + 0.7 * (avg - emai);
				if (actualema - emai > 10) {
					int sector= (int) (actualema % 4);
					//System.out.println("###AcceletometerManager##Bomb in the " + sector + "sector, "+ sum+" as sum"+avg+" as avg" + (actualema - emai) + " as differenece");
					AddBomb bomb = new AddBomb();
					Area area = null;
					switch(sector) {
					   case 0 :
					      area = Area.GREEN;
					      break; 
					   
					   case 1 :
						   area = Area.RED;
					      break; 
					   case 2 :
						   area = Area.BLUE; 
						      break;
					   case 3 :
						   area = Area.YELLOW;
						      break;
						      
					   default:
						area = Area.GREEN;
						break;
					   
					}
					
					bomb.area = area;
					observable.setActionChanged(bomb);
					
				
				}
				emai = actualema;
			}
		}

	}

}
