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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class GetWeatherInfoAPI {

    public String cityName = ":(";
    public String mainDesc = ":(";
    public String iconDesc = ":(";
    public String mainTemp = ":(";
    public String pressure = ":(";
    public String humidity = ":(";
    public String minTemp = ":(";
    public String maxTemp = ":(";
    public String windSpeed = ":(";
    public String country = ":(";
    public String sunrise = ":(";
    public String sunset = ":(";
    public double lon = 0;
    public double lat = 0;
    public boolean success = false;
    public int icon;

    public GetWeatherInfoAPI(String cityName) {
        this.cityName = cityName;
        start();
    }

    public void start() {
        new OpenWeatherMapTask(this.cityName).execute();
    }


    public String getCityName() {
        return this.cityName;
    }

    public String getMainDesc() {
        return this.mainDesc;
    }

    public int getIcon() {
        HashMap<String, Integer> icons = new HashMap<>();
        icons = WeatherTypes.getIcons(icons);
        icon = icons.get(iconDesc);
        return icon;
    }

    public String getIconDesc() {
        return iconDesc;
    }

    public String getMainTemp() {
        return mainTemp;
    }

    public String getPressure() {
        return pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getRealFeel() {
        double rf = Double.parseDouble(getMinTemp().replace(',', '.')) + Double.parseDouble(getMaxTemp().replace(',', '.')) + 3;
        String a = String.format("%.1f", rf / 2);
        return a;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getCountry() {
        return country;
    }

    public double[] getCoords() {
        double coords[] = new double[2];
        coords[0] = lat;
        coords[1] = lon;
        return coords;
    }

    public String getSunrise() {
        long millis = Long.parseLong(sunrise) * 1000;
        Date date = new Date(millis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String sunRise = simpleDateFormat.format(date);
        return sunRise;
    }

    public String getSunset() {
        long millis = Long.parseLong(sunset) * 1000;
        Date date = new Date(millis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String sunSet = simpleDateFormat.format(date);
        return sunSet;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }


    private class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {

        String city;

        String dummyAppid = "d0875d7fbed7a7af2ee449ac2d9d73ef";
        String queryWeather = "http://api.openweathermap.org/data/2.5/weather?q=";
        String queryDummyKey = "&appid=" + dummyAppid;

        OpenWeatherMapTask(String city) {
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
            HttpURLConnection httpURLConnection = (HttpURLConnection) searchURL.openConnection();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader,
                        8192);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
            }
            return result;
        }

        private String ParseJSON(String json) {
            String jsonResult = "";
            try {
                JSONObject JsonObject = new JSONObject(json);
                String cod = jsonHelperGetString(JsonObject, "cod");

                if (cod != null) {
                    if (cod.equals("200")) {
                        cityName = jsonHelperGetString(JsonObject, "name");
                        JSONObject coord = jsonHelperGetJSONObject(JsonObject, "coord");
                        if (coord != null) {
                            String lat1 = jsonHelperGetString(coord, "lat");
                            String lon1 = jsonHelperGetString(coord, "lon");
                            lat = Double.parseDouble(lat1);
                            lon = Double.parseDouble(lon1);
                        }

                        JSONObject sys = jsonHelperGetJSONObject(JsonObject, "sys");
                        if (sys != null) {
                            country = jsonHelperGetString(sys, "country");
                            sunrise = jsonHelperGetString(sys, "sunrise");
                            sunset = jsonHelperGetString(sys, "sunset");
                        }
                        JSONObject wind = jsonHelperGetJSONObject(JsonObject, "wind");
                        if (wind != null) {
                            windSpeed = jsonHelperGetString(wind, "speed");
                        }
                        if (sys != null) {
                            country = jsonHelperGetString(sys, "country");
                            sunrise = jsonHelperGetString(sys, "sunrise");
                            sunset = jsonHelperGetString(sys, "sunset");
                        }
                        jsonResult += "\n";
                        JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
                        if (weather != null) {
                            JSONObject thisWeather = weather.getJSONObject(0);
                            iconDesc = jsonHelperGetString(thisWeather, "description");
                            mainDesc = jsonHelperGetString(thisWeather, "main");
                        }

                        JSONObject main = jsonHelperGetJSONObject(JsonObject, "main");
                        if (main != null) {
                            mainTemp = toCelsius(main, "temp");
                            pressure = jsonHelperGetString(main, "pressure");
                            humidity = jsonHelperGetString(main, "humidity");
                            minTemp = toCelsius(main, "temp_min");
                            maxTemp = toCelsius(main, "temp_max");
                        }

                    }
                } else {
                    jsonResult = null;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                jsonResult = null;
            }

            return jsonResult;
        }

        private String jsonHelperGetString(JSONObject obj, String k) {
            String v = null;
            try {
                v = obj.getString(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return v;
        }

        private JSONObject jsonHelperGetJSONObject(JSONObject obj, String k) {
            JSONObject o = null;

            try {
                o = obj.getJSONObject(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return o;
        }

        private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k) {
            JSONArray a = null;

            try {
                a = obj.getJSONArray(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return a;
        }

        private String toCelsius(JSONObject main, String thing) {
            String a;
            String tmp2 = jsonHelperGetString(main, thing);
            float celsius = Float.parseFloat(tmp2);
            celsius -= 273.150;
            a = String.format("%.1f", celsius);
            return a;
        }
    }


}
