package com.povio.weatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherInfo extends AppCompatActivity {
    public String city;
    public static String willBeItemCard; //default value

    TextView textViewResult;
/*
    public WeatherInfo(String name){
        this.willBeItemCard = name;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Weather in " + willBeItemCard);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutWeatherInfo);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                refreshItems(swipeView);
            }
        });


        // TODO bg (cloud, sun, snow)
        //LinearLayout ll = (LinearLayout) findViewById(R.id.linearlayout1);
        //ll.setBackgroundResource(R.drawable.sample);
        //editTextCityName = (EditText)findViewById(R.id.cityname2);
        //btnByCityName = (Button)findViewById(R.id.bycityname2);
        textViewResult = (TextView)findViewById(R.id.result2);
        //textViewInfo = (TextView)findViewById(R.id.info2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        });
        new OpenWeatherMapTask(willBeItemCard, textViewResult).execute();
    }
    private class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {

        String cityName;
        TextView tvResult;

        String dummyAppid = "d0875d7fbed7a7af2ee449ac2d9d73ef";
        String queryWeather = "http://api.openweathermap.org/data/2.5/weather?q=";
        String queryDummyKey = "&appid=" + dummyAppid;

        OpenWeatherMapTask(String cityName, TextView tvResult){
            this.cityName = cityName;
            this.tvResult = tvResult;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            String queryReturn;

            String query;
            try {
                query = queryWeather + URLEncoder.encode(cityName, "UTF-8") + queryDummyKey;
                queryReturn = sendQuery(query);
                result += ParseJSON(queryReturn);
            } catch (Exception e) {
                e.printStackTrace();
               // queryReturn = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            tvResult.setText(s);
        }

        private String sendQuery(String query) throws IOException {
            String result = "";

            URL searchURL = new URL(query);

            HttpURLConnection httpURLConnection = (HttpURLConnection)searchURL.openConnection();
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader,
                        8192);

                String line;
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                bufferedReader.close();
            }

            return result;
        }

        private String ParseJSON(String json){
            String jsonResult = "";

            try {
                JSONObject JsonObject = new JSONObject(json);
                String cod = jsonHelperGetString(JsonObject, "cod");

                if(cod != null){
                    if(cod.equals("200")){
                        city = jsonHelperGetString(JsonObject, "name");
                        jsonResult += jsonHelperGetString(JsonObject, "name") + "\n";
                        JSONObject sys = jsonHelperGetJSONObject(JsonObject, "sys");
                        if(sys != null){
                            jsonResult += jsonHelperGetString(sys, "country") + "\n";
                        }
                        jsonResult += "\n";
                        JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
                        if(weather != null){
                            for(int i=0; i<weather.length(); i++){
                                JSONObject thisWeather = weather.getJSONObject(i);
                                jsonResult += "Weather info:\n";
                                //weatherType = jsonHelperGetString(thisWeather, "main");
                                jsonResult += jsonHelperGetString(thisWeather, "main") + "\n";
                                //weatherDesc = jsonHelperGetString(thisWeather, "description");
                                jsonResult += jsonHelperGetString(thisWeather, "description") + "\n";
                                jsonResult += "\n";
                            }
                        }

                        JSONObject main = jsonHelperGetJSONObject(JsonObject, "main");
                        if(main != null){
                            //info = toCelsius(main, "temp");
                            jsonResult += "Temperature: " + toCelsius(main, "temp") + "\n";
                            jsonResult += "Pressure: " + jsonHelperGetString(main, "pressure") + "hPa\n";
                            jsonResult += "Humidity: " + jsonHelperGetString(main, "humidity") + "%\n";
                            jsonResult += "Temperature MIN: " + toCelsius(main, "temp_min") + "\n";
                            jsonResult += "Temperature MAX: " + toCelsius(main, "temp_max") + "\n";
                            jsonResult += "\n";
                        }

                    }else if(cod.equals("404")){
                        String message = jsonHelperGetString(JsonObject, "message");
                        jsonResult += "code 404: " + message;
                    }
                }else{
                    jsonResult += "cod == null\n";
                }

            } catch (JSONException e) {
                e.printStackTrace();
                jsonResult += e.getMessage();
            }

            return jsonResult;
        }

        private String jsonHelperGetString(JSONObject obj, String k){
            String v = null;
            try {
                v = obj.getString(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return v;
        }

        private JSONObject jsonHelperGetJSONObject(JSONObject obj, String k){
            JSONObject o = null;

            try {
                o = obj.getJSONObject(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return o;
        }

        private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k){
            JSONArray a = null;

            try {
                a = obj.getJSONArray(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return a;
        }

        private String toCelsius(JSONObject main, String thing){
            String a;
            String tmp2 = jsonHelperGetString(main, thing);
            float celsius = Float.parseFloat(tmp2);
            celsius -= 273.150;
            a = String.format("%.1f", celsius);
            a += "°C";
            return a;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_city){
            Intent intent = new Intent(getBaseContext(), CityQuery.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_diag, R.anim.slide_out_diag);
        }
        if (id == R.id.home){
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }

        return super.onOptionsItemSelected(item);
    }
    public static void grantName(String willBeItemCard1){
        willBeItemCard = willBeItemCard1;
    }
    public void refreshItems(SwipeRefreshLayout swipe){
        onItemsLoadComplete(swipe);
    }
    public void onItemsLoadComplete(SwipeRefreshLayout swipe){
        grantName(willBeItemCard);
        Intent intent = new Intent(getBaseContext(), WeatherInfo.class);
        startActivity(intent);
        swipe.setRefreshing(false);
    }
    @Override
    public void onBackPressed() {
    Log.d("CDA", "onBackPressed Called");
    Intent intent = new Intent(getBaseContext(), MainActivity.class);
    startActivity(intent);
    overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

}
