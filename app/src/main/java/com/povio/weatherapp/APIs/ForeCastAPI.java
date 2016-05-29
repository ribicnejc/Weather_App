package com.povio.weatherapp.APIs;


import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ForeCastAPI {
    private ArrayList<String> mainDescL = new ArrayList<>();
    private String cityName = ":(";
    private ArrayList<String> iconDescL = new ArrayList<>();
    private ArrayList<String> mainTempL = new ArrayList<>();
    private ArrayList<String> pressureL = new ArrayList<>();
    private ArrayList<String> humidityL = new ArrayList<>();
    private ArrayList<String> minTempL = new ArrayList<>();
    private ArrayList<String> maxTempL = new ArrayList<>();
    private ArrayList<String> windSpeedL = new ArrayList<>();
    private String country = ":(";
    private ArrayList<String> sunriseL = new ArrayList<>();
    private ArrayList<String> sunsetL = new ArrayList<>();
    private ArrayList<String> dateL = new ArrayList<>();
    public boolean success = false;
    ForeCastAPI(String cityName){
        this.cityName = cityName;
        start();
    }
    public ArrayList<String> getMainDescL(){
        return mainDescL;
    }
    public ArrayList<String> getMinTempL(){
        return minTempL;
    }
    public ArrayList<String> getMaxTempL(){
        return maxTempL;
    }
    public ArrayList<String> getPressureL(){
        return pressureL;
    }
    public ArrayList<String> getHumidityL(){
        return humidityL;
    }
    public ArrayList<String> getWindSpeedL(){
        return windSpeedL;
    }
    public ArrayList<String> getDateL(){
        return dateL;
    }
    public ArrayList<String> getMainTempL(){
        return this.mainTempL;
    }
    public void start(){
        new OpenWeatherMapTask(this.cityName).execute();
    }

    private class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {

        String city;

        String dummyAppid = "d0875d7fbed7a7af2ee449ac2d9d73ef";
        String queryWeather = "http://api.openweathermap.org/data/2.5/forecast?q=";
        String queryDummyKey = "&appid=" + dummyAppid;

        public OpenWeatherMapTask(String city){
            this.city = city;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            String queryReturn;

            String query;
            try {
                query = queryWeather + URLEncoder.encode(city, "UTF-8") + queryDummyKey;
                queryReturn = sendQuery(query);
                result += ParseJSON(queryReturn);
                success = true;
            } catch (Exception e) {
                success = false;
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            success = true;
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
                        JSONArray list = jsonHelperGetJSONArray(JsonObject, "list");
                        if(list != null){
                            for(int i=0; i<list.length(); i++){
                                JSONObject thisList = list.getJSONObject(i);

                                dateL.add(jsonHelperGetString(thisList, "dt_txt"));
                                JSONObject main = jsonHelperGetJSONObject(thisList, "main");
                                mainTempL.add(toCelsius(main, "temp"));
                                minTempL.add(toCelsius(main, "temp_min"));
                                maxTempL.add(toCelsius(main, "temp_max"));
                                pressureL.add(jsonHelperGetString(main, "pressure"));
                                humidityL.add(jsonHelperGetString(main, "humidity"));
                                JSONObject wind = jsonHelperGetJSONObject(thisList, "wind");
                                windSpeedL.add(jsonHelperGetString(wind, "speed"));
                                JSONArray weather = jsonHelperGetJSONArray(thisList, "weather");
                                if (weather != null) {
                                    for (int j = 0; j < weather.length(); j++) {
                                        JSONObject thisWeather = weather.getJSONObject(j);
                                        mainDescL.add(jsonHelperGetString(thisWeather, "main"));
                                        iconDescL.add(jsonHelperGetString(thisWeather, "description"));
                                    }
                                }


                            }
                        }

                    }
                }else{
                    Log.v("API ERROR", "CANT CONNECT");
                }

            } catch (JSONException e) {
                Log.v("API ERROR", "JSONException");
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
        private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k){
            JSONArray a = null;

            try {
                a = obj.getJSONArray(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return a;
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

        private String toCelsius(JSONObject main, String thing){
            String a;
            String tmp2 = jsonHelperGetString(main, thing);
            float celsius = Float.parseFloat(tmp2);
            celsius -= 273.150;
            a = String.format("%.1f", celsius);
            return a;
        }
    }


}
