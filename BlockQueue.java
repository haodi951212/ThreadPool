import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class BlockQueue {

    private final Deque<Runnable> queue;

    private final int capacity;

    private final ReentrantLock lock;

    private final Condition fullCondition;

    private final Condition emptyCondition;

    public BlockQueue(int capacity){
        queue = new ArrayDeque<>();
        this.capacity = capacity;
        lock = new ReentrantLock();
        fullCondition = lock.newCondition();
        emptyCondition = lock.newCondition();
    }

    public Runnable poll(){
        lock.lock();
        try{
            while (queue.isEmpty()){
                try{
                    emptyCondition.await();
                }catch (InterruptedException e){

                }
            }
            Runnable task =  queue.removeFirst();
            fullCondition.signal();
            return task;
        }finally {
            lock.unlock();
        }
    }

    public Runnable pollInTime(long time, TimeUnit timeUnit){
        long miles = timeUnit.toMillis(time);
        lock.lock();
        try{
            while (queue.isEmpty()){
                try{
                    if (miles<=0){
                        return null;
                    }
                    miles = emptyCondition.awaitNanos(miles);
                }catch (InterruptedException e){

                }
            }
            Runnable task =  queue.removeFirst();
            fullCondition.signal();
            return task;
        }finally {
            lock.unlock();
        }
    }

    public void push(Runnable task){
        lock.lock();
        try{
            while (queue.size()>=capacity){
                try{
                    fullCondition.await();
                }catch (InterruptedException e){

                }
            }
            queue.addLast(task);
            emptyCondition.signal();
        }finally {
            lock.unlock();
        }
    }

    public void pushInTime(Runnable task, long time, TimeUnit timeUnit){
        long mils = timeUnit.toMillis(time);
        lock.lock();
        try{
            while (queue.size()>=capacity){
                try{
                    if (mils<=0){
                        return;
                    }
                    mils = fullCondition.awaitNanos(mils);
                }catch (InterruptedException e){

                }
            }
            queue.addLast(task);
            emptyCondition.signal();
        }finally {
            lock.unlock();
        }
    }

    public void push(Runnable task, FailPolicy policy){
        lock.lock();
        try{
            if (queue.size()>=capacity){
                policy.policy(this, task);
            }
            queue.addLast(task);
            emptyCondition.signal();
        }finally {
            lock.unlock();
        }
    }

}
