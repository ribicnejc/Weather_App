package com.povio.weatherapp;

import java.io.Serializable;


public class Data implements Serializable{
    String cityName;
    String desc;
    String type;
    String temp;

    String minTemp;
    String maxTemp;
    int weatherIcon;
    boolean refreshingState = true;
    Data(String cityName, String desc, int weatherIcon){
        this.cityName = cityName;
        this.desc = desc;
        this.weatherIcon = weatherIcon;
    }
    Data(String cityName, String desc, String type, int weatherIcon, String temp, String maxTemp, String minTemp){
        this.cityName = cityName;
        this.desc = desc;
        this.type = type;
        this.weatherIcon = weatherIcon;
        this.temp = temp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }
    public String getCityName(){
        return this.cityName;
    }
    public void setCityName(String cityName){
        this.cityName = cityName;
    }
    public String getDesc(){
        return this.desc;
    }
    public void setDesc(String desc){
        this.desc = desc;
    }
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getTemp(){
        return this.temp;
    }
    public void setTemp(String temp){
        this.temp = temp;
    }
    public String getMinTemp(){
        return this.minTemp;
    }
    public String getMaxTemp(){
        return this.maxTemp;
    }
    public void setMinTemp(String minTemp){
        this.minTemp = minTemp;
    }
    public void setMaxTemp(String maxTemp){
        this.maxTemp = maxTemp;
    }
    public void setWeatherIcon(int weatherIcon){
        this.weatherIcon = weatherIcon;
    }
    public int getWeatherIcon(){
        return this.weatherIcon;
    }
    public void refreshData(int i){
        RefreshData a = new RefreshData(this.cityName, i);
        a.start();
    }
    public boolean isRefreshing(){
        return refreshingState;
    }
    public void setRefreshingState(boolean a){
        refreshingState = a;
    }

}
