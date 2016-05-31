package com.povio.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoreInfoActivity extends AppCompatActivity {

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
        TextView cityName;
        TextView weatherDesc;
        TextView weatherInfo;
        ImageView weatherPhoto;
        ImageView firstIcon;
        TextView firstTime;
        TextView firstTemp;

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
            firstTime = (TextView) findViewById(R.id.firstTime1);
            firstTime.setText(foreCastAPI.getTimeL().get(0));
            firstIcon = (ImageView) findViewById(R.id.firstIcon1);
            firstIcon.setImageResource(foreCastAPI.getIconL().get(0));
            firstTemp = (TextView) findViewById(R.id.firstTemp1);
            firstTemp.setText(foreCastAPI.getMainTempL().get(0));
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
