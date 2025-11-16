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
        try(ExecutorService executor = Executors.newCachedThreadPool()) {
            Future<T> result = executor.submit(task);
            if(!result.isDone()) {
                System.out.println("[Info] Waiting for data...");
            }
            T value = result.get();
            if(result.isDone()) {
                System.out.println("[Info] Data is ready...");
            }
            return value;
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
