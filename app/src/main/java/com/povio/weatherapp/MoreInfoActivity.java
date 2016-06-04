package com.povio.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.povio.weatherapp.Adapters.HorizontalRVAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import com.povio.weatherapp.Images.Backgrounds;

public class MoreInfoActivity extends AppCompatActivity {
    private RecyclerView horizontalRecyclerView;
    private HorizontalRVAdapter horizontalAdapter;
    private ArrayList<String> horizontalList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetWeatherInfoAPI api;
        ForeCastAPI foreCastAPI;


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String city = extras.getString("city");
            foreCastAPI = new ForeCastAPI(city);
            api = new GetWeatherInfoAPI(city);

            waitForApi(api, foreCastAPI);
        }

    }

    public void waitForApi(final GetWeatherInfoAPI api, final ForeCastAPI foreCastAPI){
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

        try{
            cityName = (TextView) findViewById(R.id.cityMoreInfo);
            cityName.setText(api.getCityName() + ", " + api.getCountry());
            weatherInfo = (TextView) findViewById(R.id.infoMoreInfo);
            weatherInfo.setText(api.getMainDesc());
            weatherDesc = (TextView) findViewById(R.id.descMoreInfo);
            weatherDesc.setText(api.getIconDesc());
            weatherPhoto = (ImageView) findViewById(R.id.iconMoreInfo);
            weatherPhoto.setImageResource(api.getIcon());
            background = (ImageView) findViewById(R.id.background);
            HashMap<String, Integer> backgrounds = new HashMap<>();
            backgrounds = Backgrounds.getIcons(backgrounds);
            background.setImageResource(backgrounds.get(api.getIconDesc()));
            int i = 0;
            int j = 0;
            for (String elt : foreCastAPI.getTimeL()){
                if (elt.equals("12:00")){
                    int resId = getResources().getIdentifier("day" + (i+1) + "_icon", "id", getPackageName());
                    daysIcons[i] = (ImageView) findViewById(resId);
                    daysIcons[i].setImageResource(foreCastAPI.getIconL().get(j));
                    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                    Date MyDate = newDateFormat.parse(foreCastAPI.getDateAndTimeL().get(j));
                    newDateFormat.applyPattern("EEEE");

                    String nameOfDay = newDateFormat.format(MyDate);
                    int dayResId = getResources().getIdentifier("day" + (i+1), "id", getPackageName());
                    days[i] = (TextView) findViewById(dayResId);
                    days[i].setText(nameOfDay);


                    int minTempResId = getResources().getIdentifier("day"+(i+1)+"_temp_min", "id", getPackageName());
                    daysTempMin[i] = (TextView) findViewById(minTempResId);
                    daysTempMin[i].setText(foreCastAPI.getMinTempByDay(foreCastAPI.getDateAndTimeL().get(j).split(" ")[0]));

                    int maxTempResId = getResources().getIdentifier("day" + (i+1) + "_temp_max", "id", getPackageName());
                    daysTempMax[i] = (TextView) findViewById(maxTempResId);
                    daysTempMax[i].setText(foreCastAPI.getMaxTempByDay(foreCastAPI.getDateAndTimeL().get(j).split(" ")[0]));
                    i++;
                }
                j++;
            }
        }catch (Exception e){
            setContentView(R.layout.error);
            Toast.makeText(MoreInfoActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            Log.e("MoreInfoActivity","no internet connection");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_city){
            Intent intent = new Intent(getBaseContext(), CityQuery.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
        if (id == R.id.home){
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
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
}
