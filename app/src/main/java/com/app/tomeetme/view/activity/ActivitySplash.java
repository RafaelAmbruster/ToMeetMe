package com.app.tomeetme.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.tomeetme.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

public class ActivitySplash extends AppCompatActivity  {

    private Subscription mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSubscriber = Observable.timer(3, TimeUnit.SECONDS).subscribe(aLong -> {
            launchHomeScreen();
        });
    }

    private void launchHomeScreen() {
        startActivity(new Intent(this, ActivityMain.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriber.unsubscribe();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }



}
