package parallel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import matching.NYTInstanceMatcher;

public class NYTThreadPoolExecutor extends ThreadPoolExecutor{
	NYTInstanceMatcher matcher;
	
	public NYTThreadPoolExecutor(NYTInstanceMatcher matcher, int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.matcher = matcher;
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		
		SPARQLSearchThread thread = (SPARQLSearchThread) r;
		
		if(t == null) matcher.deleteRunningThread(thread.getN());
		else System.out.println("Anomaly: " + thread.getN() + "," + t);
		
	}

}
