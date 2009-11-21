package am.utility;

import am.Utility;

/**
 * A simple class that implements a run timer.
 */
public class RunTimer {

	long startTime;
	long endTime; 
	long totTime;
	
	public RunTimer() { totTime = 0; }
	
	public void start() { 
		startTime = System.nanoTime()/1000000;
	}
	public void stop() {
		endTime = System.nanoTime()/1000000;
		totTime += endTime - startTime;
	}
	public void reset() {
		totTime = 0;
	}
	public void resetAndStart() {
		reset();
		start();
	}
	
	
	public long getRunTime() {
		return totTime;
	}

	public String getFormattedRunTime() {
		long time = (System.nanoTime()/1000000) - startTime; 
		return Utility.getFormattedTime(time);
	}
	
}
