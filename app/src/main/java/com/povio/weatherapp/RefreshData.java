package com.povio.weatherapp;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;


public class RefreshData {
    public String city;
    public String desc;
    public String temp;
    public String tempMin;
    public String tempMax;
    public boolean success = false;
    public String name;
    public String description;
    public String mainDesc;
    public int icon;
    public int place;
    RefreshData(String name, String desc, int icon, int place){

        this.name = name;
        this.description = desc;
        this.icon = icon;
        this.place = place;
    }

    public void start(){
        new OpenWeatherMapTask(this.name).execute();
    }

    public void setDatas(){
        HashMap<String, Integer> we = new HashMap<>();
        we = WeatherTypes.getIcons(we);
        if (success){
            try{
                MainActivity.datas.get(this.place).setWeatherIcon(we.get(desc));
                MainActivity.datas.get(this.place).setDesc(desc);
                MainActivity.datas.get(this.place).setTemp(temp);
                MainActivity.datas.get(this.place).setMaxTemp(tempMax);
                MainActivity.datas.get(this.place).setMinTemp(tempMin);
                MainActivity.datas.get(this.place).setType(mainDesc);
                MainActivity.datas.get(this.place).setRefreshingState(false);
            }catch(Exception e){
                MainActivity.datas.get(this.place).setWeatherIcon(this.icon);
                MainActivity.datas.get(this.place).setDesc(this.description);
                MainActivity.datas.get(this.place).setRefreshingState(false);
            }

        }
    }
    private class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {

        String cityName;
        //TextView tvResult;

        String dummyAppid = "d0875d7fbed7a7af2ee449ac2d9d73ef";
        String queryWeather = "http://api.openweathermap.org/data/2.5/weather?q=";
        String queryDummyKey = "&appid=" + dummyAppid;

        OpenWeatherMapTask(String cityName){
            this.cityName = cityName;
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
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            setDatas();
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
                                desc = jsonHelperGetString(thisWeather, "description");
                                mainDesc = jsonHelperGetString(thisWeather, "main");
                            }
                        }

                        JSONObject main = jsonHelperGetJSONObject(JsonObject, "main");
                        if(main != null){
                            temp = toCelsius(main, "temp");
                            tempMin = toCelsius(main, "temp_min");
                            tempMax = toCelsius(main, "temp_max");
                        }

                    }
                }else{
                    jsonResult = null;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                jsonResult = null;
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
            //a += "°C";
            return a;
        }
    }



}
