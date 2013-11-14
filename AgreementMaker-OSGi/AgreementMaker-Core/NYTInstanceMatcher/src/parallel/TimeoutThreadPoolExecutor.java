package parallel;
import java.util.List;
import java.util.concurrent.*;

public class TimeoutThreadPoolExecutor extends ThreadPoolExecutor {
    private final long timeout;
    private final TimeUnit timeoutUnit;

    private final ScheduledExecutorService timeoutExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentMap<Runnable, ScheduledFuture> runningTasks = new ConcurrentHashMap<Runnable, ScheduledFuture>();

    public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, long timeout, TimeUnit timeoutUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, long timeout, TimeUnit timeoutUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler, long timeout, TimeUnit timeoutUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, long timeout, TimeUnit timeoutUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    @Override
    public void shutdown() {
        timeoutExecutor.shutdown();
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        timeoutExecutor.shutdownNow();
        return super.shutdownNow();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if(timeout > 0) {
            final ScheduledFuture<?> scheduled = timeoutExecutor.schedule(new TimeoutTask(t), timeout, timeoutUnit);
            runningTasks.put(r, scheduled);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        ScheduledFuture timeoutTask = runningTasks.remove(r);
        if(timeoutTask != null) {
            timeoutTask.cancel(false);
        }
    }

    class TimeoutTask implements Runnable {
        private final Thread thread;

        public TimeoutTask(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            thread.interrupt();
        }
    }
}