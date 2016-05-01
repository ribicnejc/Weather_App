package com.povio.weatherapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.Calendar;
import java.util.Locale;

public class Splash extends AppCompatActivity {

    protected int _splashTime = 2500;
    ShimmerTextView tv;
    Shimmer shimmer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        Typeface type = Typeface.createFromAsset(getAssets(), "openSansLight.ttf");
        tv.setTypeface(type);
        shimmer = new Shimmer();
        shimmer.setDuration(2000);
        shimmer.start(tv);
        TextView version = (TextView)findViewById(R.id.version);
        version.setTypeface(type);
        Calendar c = Calendar.getInstance();
        String timeStamp = String.format(Locale.getDefault(), "%d", c.get(Calendar.HOUR_OF_DAY));
        int time = Integer.parseInt(timeStamp);
        if (time > 0 && time <= 4){
            tv.setText("Good Morning");
        }
        if (time > 4 && time <= 8){
            tv.setText("Good Morning");
        }
        if (time > 8 && time < 12){
            tv.setText("Good Morning");
        }
        if (time >= 12 && time < 18){
            tv.setText("Good Afternoon");
        }
        if (time >= 18 && time < 24){
            tv.setText("Good Evening");
        }

        //tv.setText(timeStamp);
        new Thread() {

            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(_splashTime);
                    } catch (InterruptedException e) {
                        Log.d("Error", "error splash");
                    } finally {
                        finish();
                        //shimmer.cancel();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                }
            }

        }.start();
    }
}
