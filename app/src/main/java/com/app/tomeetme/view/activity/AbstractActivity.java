package com.app.tomeetme.view.activity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.util.Tools;

import java.util.LinkedList;
import java.util.List;

public class AbstractActivity extends AppCompatActivity {

    private final Object mLock = new Object();
    private Boolean mReady = false;
    private List<Runnable> mPendingCallbacks = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        synchronized (mLock) {
            mReady = true;
            int pendingCallbacks = mPendingCallbacks.size();
            while (pendingCallbacks-- > 0) {
                Runnable runnable = mPendingCallbacks.remove(0);
                runNow(runnable);
            }
        }

        Tools.systemBarLolipop(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        synchronized (mLock) {
            mReady = false;
        }
    }

    protected void runTaskCallback(Runnable runnable) {
        if (mReady) runNow(runnable);
        else addPending(runnable);
    }

    protected void executeTask(AsyncTask<Void, ?, ?> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    private void runNow(Runnable runnable) {
        runOnUiThread(runnable);
    }

    private void addPending(Runnable runnable) {
        synchronized (mLock) {
            mPendingCallbacks.add(runnable);
        }
    }
}
