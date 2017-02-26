package com.ll.progressbar;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ll.progressbar.view.ProgressView;

public class MainActivity extends AppCompatActivity {
    private ProgressView mProgressBar;
    private ProgressView mProgressBar1;
    private ProgressView mProgressBar2;
    private HandlerThread thread;
    private Handler mHandler;
    private int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressView) findViewById(R.id.progressView1);
        mProgressBar1 = (ProgressView) findViewById(R.id.progressView2);
        mProgressBar2 = (ProgressView) findViewById(R.id.progressView3);

        HandlerThread thread  = new HandlerThread("handler_thread");
        thread.start();
        mHandler = new Handler(thread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(mProgressBar1.getProgress() == mProgressBar1.getMaxProgress()){
                    mHandler.removeMessages(0);
                    return;
                }
                mProgressBar.incrementProgressBy(1);
                mProgressBar1.incrementProgressBy(1);
                mProgressBar2.incrementProgressBy(1);
                mHandler.sendEmptyMessageDelayed(0,200);
            }
        };
        mHandler.sendEmptyMessage(0);
    }
}
