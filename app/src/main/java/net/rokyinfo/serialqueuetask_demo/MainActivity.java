package net.rokyinfo.serialqueuetask_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import net.rokyinfo.task.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RKTaskQueue mRKTaskQueue = new RKTaskQueue();
        mRKTaskQueue.start();
        mRKTaskQueue.add(new RKTask() {
            @Override
            public void execute() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("RKTask", "1");
            }
        });
        mRKTaskQueue.add(new RKTask() {
            @Override
            public void execute() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("RKTask", "2");
            }
        });

        mRKTaskQueue.add(new RKTask() {
            @Override
            public void execute() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("RKTask", "3");
            }
        });
    }
}
