package com.povio.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.romainpiel.shimmer.ShimmerButton;

public class MoreInfoActivity extends AppCompatActivity {

    Context mContext;
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
        ImageView firstIcon;
        TextView firstTime;
        TextView firstTemp;

        try{
        cityName = (TextView) findViewById(R.id.cityMoreInfo);
        cityName.setText(api.getCityName() + ", " + api.getCountry());
        weatherInfo = (TextView) findViewById(R.id.infoMoreInfo);
        weatherInfo.setText(api.getMainDesc());
        weatherDesc = (TextView) findViewById(R.id.descMoreInfo);
        weatherDesc.setText(api.getIconDesc());
        weatherPhoto = (ImageView) findViewById(R.id.iconMoreInfo);
        weatherPhoto.setImageResource(api.getIcon());
//        temp = (TextView) findViewById(R.id.tempMoreInfo);
//        temp.setText(api.getMainTemp());
//        minTemp = (TextView) findViewById(R.id.minTempMoreInfo);
//        minTemp.setText(api.getMinTemp() + "°C");
//        maxTemp = (TextView) findViewById(R.id.maxTempMoreInfo);
//        maxTemp.setText(api.getMaxTemp() + "°C");
//        humidity = (TextView) findViewById(R.id.humidityMoreInfo);
//        humidity.setText(api.getHumidity() + "%");
//        pressure = (TextView) findViewById(R.id.pressureMoreInfo);
//        pressure.setText(api.getPressure() + " hPa");
//        realFeel = (TextView) findViewById(R.id.realFeelMoreInfo);
//        realFeel.setText("Real feel: " + api.getRealFeel() + "°C");

        firstTime = (TextView) findViewById(R.id.firstTime);
        firstTime.setText(foreCastAPI.getTimeL().get(0));
        firstIcon = (ImageView) findViewById(R.id.firstIcon);
        firstIcon.setImageResource(foreCastAPI.getIconL().get(0));
        firstTemp = (TextView) findViewById(R.id.firstTemp);
        firstTemp.setText(foreCastAPI.getMainTempL().get(0));


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
