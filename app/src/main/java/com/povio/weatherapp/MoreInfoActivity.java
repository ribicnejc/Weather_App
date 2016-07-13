package com.povio.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.povio.weatherapp.Adapters.HorizontalRVAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.povio.weatherapp.Images.Backgrounds;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class MoreInfoActivity extends AppCompatActivity {
    private RecyclerView horizontalRecyclerView;
    private HorizontalRVAdapter horizontalAdapter;
    private Toolbar toolbar;
    private String cityName;
    private ArrayList<String> horizontalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_before_moreinfo);

        ProgressBar pr;


        pr = (ProgressBar) findViewById(R.id.progressBarMoreInfo);
        pr.setVisibility(View.VISIBLE);
        GetWeatherInfoAPI api;
        ForeCastAPI foreCastAPI;


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cityName = extras.getString("city");

            foreCastAPI = new ForeCastAPI(cityName);
            api = new GetWeatherInfoAPI(cityName);

            waitForApi(api, foreCastAPI);
        }

    }

    public void waitForApi(final GetWeatherInfoAPI api, final ForeCastAPI foreCastAPI) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (api.success && foreCastAPI.success) {
                    onFinish(api, foreCastAPI);
                } else {
                    waitForApi(api, foreCastAPI);
                }
            }
        }, 100);
    }

    public void onFinish(GetWeatherInfoAPI api, ForeCastAPI foreCastAPI) {
        setContentView(R.layout.activity_weather_info_update);
        Typeface type = Typeface.createFromAsset(getAssets(), "openSansLight.ttf");
        toolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Weather in " + api.getCityName());
        horizontalRecyclerView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        horizontalAdapter = new HorizontalRVAdapter(foreCastAPI.getTimeL(), foreCastAPI.getIconL(), foreCastAPI.getMainTempL());
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        TextView cityName;
        TextView weatherDesc;
        TextView weatherInfo;
        ImageView weatherPhoto;
        ImageView firstIcon;
        TextView firstTime;
        TextView firstTemp;
        ImageView background;

        TextView[] days = new TextView[5];
        TextView[] daysTempMin = new TextView[5];
        TextView[] daysTempMax = new TextView[5];
        ImageView[] daysIcons = new ImageView[5];

        try {
            cityName = (TextView) findViewById(R.id.cityMoreInfo);
            cityName.setText(api.getCityName() + ", " + api.getCountry());
            cityName.setTypeface(type);

            weatherInfo = (TextView) findViewById(R.id.infoMoreInfo);
            weatherInfo.setText(api.getMainDesc());
            weatherInfo.setTypeface(type);

            weatherDesc = (TextView) findViewById(R.id.descMoreInfo);
            weatherDesc.setText(api.getIconDesc());
            weatherDesc.setTypeface(type);

            weatherPhoto = (ImageView) findViewById(R.id.iconMoreInfo);
            weatherPhoto.setImageResource(api.getIcon());
            background = (ImageView) findViewById(R.id.background);
            HashMap<String, Integer> backgrounds = new HashMap<>();
            backgrounds = Backgrounds.getIcons(backgrounds);
            background.setImageBitmap(
                    decodeSampledBitmapFromResource(getResources(), R.id.background, 100, 100)
            );
            background.setImageResource(backgrounds.get(api.getIconDesc()));
            int i = 0;
            int j = 0;
            for (String elt : foreCastAPI.getTimeL()) {
                if (elt.equals("15:00")) {
                   // RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.day1_main);
                    int resId = getResources().getIdentifier("day" + (i + 1) + "_icon", "id", getPackageName());
                    daysIcons[i] = (ImageView) findViewById(resId);
                    daysIcons[i].setImageResource(foreCastAPI.getIconL().get(j));
                    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                    Date MyDate = newDateFormat.parse(foreCastAPI.getDateAndTimeL().get(j));
                    newDateFormat.applyPattern("EEEE");


                    int dayMainId = getResources().getIdentifier("day" + (i + 1) + "_main", "id", getPackageName());
                    RelativeLayout relativeLayout1 = (RelativeLayout) findViewById(dayMainId);
                    relativeLayout1.setVisibility(View.VISIBLE);

                    String nameOfDay = newDateFormat.format(MyDate);
                    int dayResId = getResources().getIdentifier("day" + (i + 1), "id", getPackageName());
                    days[i] = (TextView) findViewById(dayResId);
                    days[i].setText(nameOfDay);
                    days[i].setTypeface(type);


                    int minTempResId = getResources().getIdentifier("day" + (i + 1) + "_temp_min", "id", getPackageName());
                    daysTempMin[i] = (TextView) findViewById(minTempResId);
                    daysTempMin[i].setText(foreCastAPI.getMinTempByDay(foreCastAPI.getDateAndTimeL().get(j).split(" ")[0]));
                    daysTempMin[i].setTypeface(type);

                    int maxTempResId = getResources().getIdentifier("day" + (i + 1) + "_temp_max", "id", getPackageName());
                    daysTempMax[i] = (TextView) findViewById(maxTempResId);
                    daysTempMax[i].setText(foreCastAPI.getMaxTempByDay(foreCastAPI.getDateAndTimeL().get(j).split(" ")[0]));
                    daysTempMax[i].setTypeface(type);
                    i++;

                }
                j++;
            }
        } catch (Exception e) {
            setContentView(R.layout.error);
            Toast.makeText(MoreInfoActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            Log.e("MoreInfoActivity", "Oops something went wrong");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem sortIcon = menu.findItem(R.id.sort_icon);
        //MenuItem searchIcon = menu.findItem(R.id.search);
        sortIcon.setVisible(false);
        //searchIcon.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_city) {
            Intent intent = new Intent(getBaseContext(), CityQuery.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
        if (id == R.id.homeSet) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }
        if (id == android.R.id.home){
            startActivity(new Intent(getBaseContext(), MainActivity.class));
           // NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }
        if (id == R.id.refresh_toolbar_icon){
            Intent intent = new Intent(getBaseContext(), MoreInfoActivity.class);
            intent.putExtra("city", cityName);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
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
