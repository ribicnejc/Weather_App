package com.povio.weatherapp.Images;

import com.povio.weatherapp.R;

import java.util.HashMap;

/**
 * Created by Nejc on 3. 06. 2016.
 */
public class Backgrounds {

    public static HashMap<String, Integer> getIcons(HashMap typeIcons){
        // HashMap<String, Integer> typeIcons = new HashMap<String, Integer>();
        // sky
        typeIcons.put("Sky is Clear", R.drawable.bg_clear);
        typeIcons.put("sky is clear", R.drawable.bg_clear);
        typeIcons.put("clear sky", R.drawable.bg_clear);
        // rains
        typeIcons.put("moderate rain", R.drawable.bg_rain);
        typeIcons.put("light rain", R.drawable.bg_rain);
        typeIcons.put("light intensity drizzle", R.drawable.bg_rain2);
        typeIcons.put("heavy intensity rain", R.drawable.bg_rain2);
        typeIcons.put("very heavy rain", R.drawable.bg_rain);
        typeIcons.put("extreme rain", R.drawable.bg_rain2);
        typeIcons.put("freezing rain", R.drawable.bg_rain);
        typeIcons.put("shower rain", R.drawable.bg_rain2);
        typeIcons.put("heavy intensity shower rain", R.drawable.bg_rain);
        typeIcons.put("ragged shower rain", R.drawable.bg_rain);
        typeIcons.put("light intensity shower rain", R.drawable.bg_rain2);
        //storms
        typeIcons.put("thunderstorm", R.drawable.bg_storm);
        // clouds
        typeIcons.put("few clouds", R.drawable.bg_partly_cloud);
        typeIcons.put("scattered clouds", R.drawable.bg_clouds);
        typeIcons.put("broken clouds", R.drawable.bg_partly_cloud);
        typeIcons.put("overcast clouds", R.drawable.bg_clouds);
        // snows
        typeIcons.put("light snow", R.drawable.bg_storm);
        typeIcons.put("snow", R.drawable.bg_snow);
        typeIcons.put("heavy snow", R.drawable.bg_snow);
        typeIcons.put("light rain and snow", R.drawable.bg_snow);
        //fog
        typeIcons.put("fog", R.drawable.bg_clouds);
        typeIcons.put("mist", R.drawable.bg_clouds);
        typeIcons.put("haze", R.drawable.bg_clouds);
        typeIcons.put("smoke", R.drawable.bg_clouds);
        typeIcons.put("light shower snow", R.drawable.bg_snow);
        //error
        typeIcons.put("error", R.drawable.error);


        // TODO add more icons
        return typeIcons;

    }
}
