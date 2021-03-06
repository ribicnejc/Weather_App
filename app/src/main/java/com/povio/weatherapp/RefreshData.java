package com.povio.weatherapp;

import android.os.Handler;


public class RefreshData {
    public String name;
    private int place;
    public GetWeatherInfoAPI api;
    RefreshData(String name, int place){
        this.name = name;
        this.place = place;
    }

    public void start(){
        api = new GetWeatherInfoAPI(this.name);
        waitForApi(api);
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
    public void onFinish(GetWeatherInfoAPI api){
        if(!api.getMainDesc().equals(":(")){
            MainActivity.datas.get(this.place).setType(api.getMainDesc());
            MainActivity.datas.get(this.place).setTemp(api.getMainTemp());
            MainActivity.datas.get(this.place).setWeatherIcon(api.getIcon());
            MainActivity.datas.get(this.place).setMinTemp(api.getMinTemp());
            MainActivity.datas.get(this.place).setMaxTemp(api.getMaxTemp());
        }
    }

}

