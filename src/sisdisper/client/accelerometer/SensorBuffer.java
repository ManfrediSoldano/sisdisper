package sisdisper.client.accelerometer;

import java.util.ArrayList;
import java.util.List;

public class SensorBuffer implements Buffer{

	ArrayList<Measurement> measure = new ArrayList<Measurement>();
	@Override
	public void addNewMeasurement(Measurement t) {
		synchronized(measure){
			//System.out.println("Added measurement");

			measure.add(t);
		}
		
	}

	@Override
	public ArrayList readAllAndClean() {
		ArrayList<Measurement> temp = new ArrayList<Measurement>();
		
		synchronized(measure){
			for(Measurement measurement: measure){
				temp.add(measurement);
			}
			measure.clear();
		}
		
		return temp;
	}

}
