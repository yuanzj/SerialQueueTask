package net.rokyinfo.task;

import android.os.*;
import android.os.Process;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by YuanZhiJian on 16/4/3.
 */
public class RKTaskDispatcher extends Thread {

    private final BlockingQueue<RKTask> mQueue;

    private final RKTaskQueue mRKTaskQueue;

    private volatile boolean mQuit = false;

    public RKTaskDispatcher(BlockingQueue<RKTask> mQueue,RKTaskQueue mRKTaskQueue){

        this.mQueue = mQueue;
        this.mRKTaskQueue = mRKTaskQueue;

    }

    public void quit(){
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        RKTask task;
        while (true) {
            long startTimeMs = SystemClock.elapsedRealtime();
            // release previous task object to avoid leaking request object when mQueue is drained.
            task = null;
            try {
                // Take a request from the queue.
                task = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                if (task.isCanceled()) {
                    continue;
                }
                task.execute();
                mRKTaskQueue.finish(task);
            } catch (Exception e) {

                Log.e("Unhandled exception %s",e.toString());

            }
        }
    }
}
