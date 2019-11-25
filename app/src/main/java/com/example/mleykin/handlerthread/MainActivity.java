package com.example.mleykin.handlerthread;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Handler mUiHandler = new Handler();
    private MyWorkerThread mWorkerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWorkerThread = new MyWorkerThread("myWorkerThread");
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Background task1 is completed",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        };
        Runnable task2 = new Runnable() {
            @Override
            public void run() {
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Background task2 is completed",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        };
        mWorkerThread.start();
        mWorkerThread.prepareHandler();
        for (int i=0; i<10; i++) {
            mWorkerThread.postTask(task1);
            mWorkerThread.postTask(task2);
        }
    }

    @Override
    protected void onDestroy() {
        mWorkerThread.quit();
        super.onDestroy();
    }
}
