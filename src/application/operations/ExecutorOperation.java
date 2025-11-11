package application.operations;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;

public class ExecutorOperation {
    
    /**
     * Helper to execute a callable task with virtual threads.
     * @param task the Callable task to execute 
     * @throws InterruptedException if the executor service can't start
     * @throws CancellationException if the future of the task was canceled.
     * @return the result of the execution of the generic type T
     */
    public <T> T executeConcurrentCallableList(Callable<T> task) {
        T value = null;
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            Future<T> result = executor.submit(task);
            if(!result.isDone()) {
                System.out.println("[Info] Waiting for data...");
            }
            value = result.get();
            if(result.isDone()) {
                System.out.println("[Info] Data is ready...");
            }
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            try {
                if(!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

}
