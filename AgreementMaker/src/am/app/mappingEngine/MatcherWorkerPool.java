package am.app.mappingEngine;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Replaces SwingWorker in order to allow a matcher to be executed multiple
 * times from the same object.  These changes are necessary in order to adapt 
 * our framework to the OSGi framework.
 * 
 * @author joe
 *
 */
public class MatcherWorkerPool {

	private boolean isCancelled;
	private PropertyChangeSupport propertyChangeSupport;
	
	//make the thread pool
	private ThreadPoolExecutor matcherPool;
	
	//make a queue
	final LinkedBlockingQueue <Runnable> matcherQueue = new LinkedBlockingQueue <Runnable>();
	
	private int progress = 0;
	
	public MatcherWorkerPool(){
		matcherPool = new ThreadPoolExecutor(1,10,5L,TimeUnit.SECONDS, matcherQueue);
	}
	
	
	
	
	
	public boolean isCancelled() { return isCancelled; }
	
	public void cancel(boolean interrupt){
		isCancelled=true;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}
	
	public void setProgress(int progress) {
		propertyChangeSupport.firePropertyChange("progress", this.progress, progress);
		this.progress = progress;
	}
	
	//do not think this is needed
	public void addToQueue(Runnable matcher){
		try {
			matcherQueue.put(matcher);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public void shutDown()
    {
        matcherPool.shutdown();
    }
	
	//takes a runnable that is a matcher and executes it
	public void execute(Runnable matcher) {
		matcherPool.execute(matcher);
	}
	
}
