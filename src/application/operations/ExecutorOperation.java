package application.operations;

import java.util.concurrent.*;

public class ExecutorOperation {
    
    /**
     * Helper to execute a callable task with virtual threads.
     * @param task - the Callable task to execute 
     * @return the result of the execution of the generic type T
     */
    public <T> T executeConcurrentCallableList(Callable<T> task) {
        try(ExecutorService executor = Executors.newCachedThreadPool()) {
            Future<T> result = executor.submit(task);
            if(!result.isDone()) {
                System.console().printf("%s%n", "[Info] Waiting for data...");
            }
            T value = result.get();
            if(result.isDone()) {
                System.console().printf("%s%n", "[Info] Data is ready...");
            }
            return value;
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

}
