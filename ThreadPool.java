import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public final class ThreadPool {

    private final BlockQueue blockQueue;

    private final HashSet<Worker> workers;

    private final int size;

    private final long time;

    private final TimeUnit timeUnit;

    public ThreadPool(int capacity, int size, long time, TimeUnit timeUnit) {
        blockQueue = new BlockQueue(capacity);
        workers = new HashSet<>(size);
        this.size = size;
        this.time = time;
        this.timeUnit = timeUnit;
    }

    public synchronized void Execute(Runnable task){
        if (workers.size()<size){
            Worker worker = new Worker(task);
            workers.add(worker);
            worker.start();
        }else {
            blockQueue.push(task);
        }
    }

    public synchronized void Execute(Runnable task, FailPolicy policy){
        if (workers.size()<size){
            Worker worker = new Worker(task);
            workers.add(worker);
            worker.start();
        }else {
            blockQueue.push(task, policy);
        }
    }


    private class Worker extends Thread{
        Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            while(true){
                if (task == null || (task=blockQueue.pollInTime(time, timeUnit)) == null){
                    return;
                }
                task.run();
            }
        }
    }
}
