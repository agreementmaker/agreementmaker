package am.utility;

import am.Utility;

/**
 * A simple class that implements a run timer.
 */
public class RunTimer {

	private long startTime;
	private long endTime; 
	private long totTime;
	private boolean stopped = false;
	
	private static final long MILLI = 1000000l;
	
	public RunTimer() { totTime = 0; }
	
	public RunTimer start() { 
		startTime = System.nanoTime() / MILLI;
		stopped = false;
		return this;
	}
	public RunTimer stop() {
		getRunTime();
		stopped = true;
		return this;
	}
	public RunTimer reset() {
		endTime = startTime = totTime = 0;
		stopped = true;
		return this;
	}
	public RunTimer resetAndStart() {
		reset();
		start();
		return this;
	}
	
	/**
	 * @return The elapsed time since the timer was started, in milliseconds.
	 */
	public long getRunTime() {
		if( !stopped ) {
			endTime = System.nanoTime() / MILLI;
			totTime = endTime - startTime;
		}
		return totTime;
	}

	public String getFormattedRunTime() {
		return Utility.getFormattedTime(getRunTime());
	}
	
	@Override
	public String toString() {
		return getFormattedRunTime();
	}
}
