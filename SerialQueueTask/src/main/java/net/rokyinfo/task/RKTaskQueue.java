package net.rokyinfo.task;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by YuanZhiJian on 16/4/3.
 */
public class RKTaskQueue {

    private static final int DEFAULT_TASK_THREAD_POOL_SIZE = 1;

    private final Set<RKTask> mCurrentTasks = new HashSet<RKTask>();

    private final PriorityBlockingQueue<RKTask> mRKTaskQueue = new PriorityBlockingQueue<RKTask>();

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    private RKTaskDispatcher[] mDispatchers;

    public RKTaskQueue(){

        mDispatchers = new RKTaskDispatcher[DEFAULT_TASK_THREAD_POOL_SIZE];

    }

    public RKTaskQueue(int threadPoolSize){

        mDispatchers = new RKTaskDispatcher[threadPoolSize];

    }

    public RKTask add(RKTask task) {
        synchronized (mCurrentTasks) {
            mCurrentTasks.add(task);
        }
        task.setSequence(getSequenceNumber());
        mRKTaskQueue.add(task);
        return task;
    }



    public void start(){

        stop();
        for (int i = 0; i < mDispatchers.length; i++) {
            RKTaskDispatcher networkDispatcher = new RKTaskDispatcher(mRKTaskQueue,this);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }

    }

    public void stop(){

        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].quit();
            }
        }

    }

    public interface TaskFilter {
        public boolean apply(RKTask task);
    }

    /**
     * Cancels all requests in this queue for which the given filter applies.
     * @param filter The filtering function to use
     */
    public void cancelAll(TaskFilter filter) {
        synchronized (mCurrentTasks) {
            for (RKTask task : mCurrentTasks) {
                if (filter.apply(task)) {
                    task.cancel();
                }
            }
        }
    }

    /**
     * Cancels all requests in this queue with the given tag. Tag must be non-null
     * and equality is by identity.
     */
    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new TaskFilter() {
            @Override
            public boolean apply(RKTask task) {
                return task.getTag() == tag;
            }
        });
    }

    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    void finish(RKTask task) {
        synchronized (mCurrentTasks) {
            mCurrentTasks.remove(task);
        }
    }
}
