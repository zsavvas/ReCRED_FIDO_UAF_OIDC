package eu.recred.fidouafjava.client.util;

import java.util.concurrent.*;

/**
 * Created by sorin on 21.08.2016.
 */
public class ThreadRunner {
    private static ThreadRunner mInstance;
    private ExecutorService mExecutor;

    private ThreadRunner() {
        mExecutor = Executors.newWorkStealingPool();
    }

    public static ThreadRunner getInstance() {
        if (mInstance == null)
            mInstance = new ThreadRunner();
        return mInstance;
    }

    public Future<String> submitCallable(Callable<String> callable) {
        return mExecutor.submit(callable);
    }

    public void stop() {
        if (mExecutor.isShutdown())
            return;
        try {
            // System.out.println("attempt to shutdown executor");
            mExecutor.shutdown();
            mExecutor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            // System.err.println("tasks interrupted");

        }
        finally {
            if (mExecutor.isTerminated()) {
                // System.err.println("cancel non-finished tasks");
            }
            mExecutor.shutdownNow();
            // System.out.println("shutdown finished");
        }
    }
}
