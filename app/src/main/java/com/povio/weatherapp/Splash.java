package com.povio.weatherapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
        ImageView background = (ImageView) findViewById(R.id.background);
        View view = findViewById(R.id.background_view);
        if (view != null)
            view.setVisibility(View.GONE);
        if (background != null)
            background.setImageBitmap(
                    decodeSampledBitmapFromResource(getResources(), R.id.background, 100, 100)
            );
        if (background != null)
            background.setImageResource(R.drawable.splash_background);
        tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        Typeface type = Typeface.createFromAsset(getAssets(), "openSansLight.ttf");
        tv.setTypeface(type);
        shimmer = new Shimmer();
        shimmer.setDuration(2000);
        shimmer.start(tv);
        TextView version = (TextView) findViewById(R.id.version);
        version.setTypeface(type);
        Calendar c = Calendar.getInstance();
        String timeStamp = String.format(Locale.getDefault(), "%d", c.get(Calendar.HOUR_OF_DAY));
        int time = Integer.parseInt(timeStamp);
        if (time > 0 && time <= 4) {
            tv.setText("Good Morning");
        }
        if (time > 4 && time <= 8) {
            tv.setText("Good Morning");
        }
        if (time > 8 && time < 12) {
            tv.setText("Good Morning");
        }
        if (time >= 12 && time < 18) {
            tv.setText("Good Afternoon");
        }
        if (time >= 18 && time < 24) {
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

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
