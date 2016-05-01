package com.povio.weatherapp;


import java.util.HashMap;

/**
 * Created by Nejc on 8. 02. 2016.
 */
public class WeatherTypes {
    public static HashMap<String, Integer> getIcons(HashMap typeIcons){
       // HashMap<String, Integer> typeIcons = new HashMap<String, Integer>();
        // sky
        typeIcons.put("Sky is Clear", R.drawable.sun);
        typeIcons.put("sky is clear", R.drawable.sun);
        typeIcons.put("clear sky", R.drawable.sun);
        // rains
        typeIcons.put("moderate rain", R.drawable.rain);
        typeIcons.put("light rain", R.drawable.light_rain);
        typeIcons.put("light intensity drizzle", R.drawable.clouds);
        typeIcons.put("heavy intensity rain", R.drawable.intense_rain);
        typeIcons.put("very heavy rain", R.drawable.downpour);
        typeIcons.put("extreme rain", R.drawable.downpour);
        typeIcons.put("freezing rain", R.drawable.hail);
        typeIcons.put("shower rain", R.drawable.downpour);
        typeIcons.put("heavy intensity shower rain", R.drawable.downpour);
        typeIcons.put("ragged shower rain", R.drawable.rain);
        typeIcons.put("light intensity shower rain", R.drawable.heavy_rain);
        // clouds
        typeIcons.put("few clouds", R.drawable.partly_cloudy_day);
        typeIcons.put("scattered clouds", R.drawable.cloud);
        typeIcons.put("broken clouds", R.drawable.partly_cloudy_day);
        typeIcons.put("overcast clouds", R.drawable.clouds);
        // snows
        typeIcons.put("light snow", R.drawable.light_snow);
        typeIcons.put("snow", R.drawable.snow);
        typeIcons.put("heavy snow", R.drawable.snow_storm);
        typeIcons.put("light rain and snow", R.drawable.sleet);
        //fog
        typeIcons.put("fog", R.drawable.fog_day);
        typeIcons.put("mist", R.drawable.fog_day);
        typeIcons.put("haze", R.drawable.moisture);
        typeIcons.put("smoke", R.drawable.dust);
        typeIcons.put("light shower snow", R.drawable.sleet);
        //error
        typeIcons.put("error", R.drawable.error);


        // TODO add more icons
        return typeIcons;

    }

}
