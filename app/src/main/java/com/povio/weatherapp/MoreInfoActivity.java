package com.povio.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

public class MoreInfoActivity extends AppCompatActivity {

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetWeatherInfoAPI api;
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String city = extras.getString("city");
            api = new GetWeatherInfoAPI(city);
            waitForApi(api);
        }

    }

    public void waitForApi(final GetWeatherInfoAPI api){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (api.success) {
                    onFinish(api);
                } else {
                    waitForApi(api);
                }
            }
        }, 100);
    }
    public void onFinish(GetWeatherInfoAPI api) {
        setContentView(R.layout.activity_weather_info_update);
        TextView cityName;
        TextView sunset;
        TextView sunrise;
        TextView minTemp;
        TextView maxTemp;
        TextView temp;
        TextView humidity;
        TextView pressure;
        TextView weatherDesc;
        TextView weatherInfo;
        TextView realFeel;
        ImageView weatherPhoto;
        ShimmerButton button;
        try{
        cityName = (TextView) findViewById(R.id.cityMoreInfo);
        cityName.setText(api.getCityName() + ", " + api.getCountry());
        weatherInfo = (TextView) findViewById(R.id.infoMoreInfo);
        weatherInfo.setText(api.getMainDesc());
        weatherDesc = (TextView) findViewById(R.id.descMoreInfo);
        weatherDesc.setText(api.getIconDesc());
        sunrise = (TextView) findViewById(R.id.sunriseMoreInfo);
        sunrise.setText(api.getSunrise());
        sunset = (TextView) findViewById(R.id.sunsetMoreInfo);
        sunset.setText(api.getSunset());
        weatherPhoto = (ImageView) findViewById(R.id.iconMoreInfo);
        weatherPhoto.setImageResource(api.getIcon());
        temp = (TextView) findViewById(R.id.tempMoreInfo);
        temp.setText(api.getMainTemp());
        minTemp = (TextView) findViewById(R.id.minTempMoreInfo);
        minTemp.setText(api.getMinTemp() + "°C");
        maxTemp = (TextView) findViewById(R.id.maxTempMoreInfo);
        maxTemp.setText(api.getMaxTemp() + "°C");
        humidity = (TextView) findViewById(R.id.humidityMoreInfo);
        humidity.setText(api.getHumidity() + "%");
        pressure = (TextView) findViewById(R.id.pressureMoreInfo);
        pressure.setText(api.getPressure() + " hPa");
        realFeel = (TextView) findViewById(R.id.realFeelMoreInfo);
        realFeel.setText("Real feel: " + api.getRealFeel() + "°C");
        button = (ShimmerButton) findViewById(R.id.backMoreInfo);
        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(2000);
        shimmer.start(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        });
        }catch (Exception e){
            setContentView(R.layout.error);
            Toast.makeText(MoreInfoActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            Log.e("MoreInfoActivity","no internet connection");
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }
}
