package application.operations;

import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

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
        try (ExecutorService ex = Executors.newCachedThreadPool()) {
            Future<T> result = ex.submit(task);
            if(result.state() == Future.State.RUNNING) {
                System.out.println("[Info] Waiting for data...");
            }
            value = result.get();
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            e.printStackTrace();
        }
        return value;
    }

}
