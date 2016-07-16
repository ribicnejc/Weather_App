package com.povio.weatherapp.Widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.povio.weatherapp.ForeCastAPI;
import com.povio.weatherapp.GetWeatherInfoAPI;
import com.povio.weatherapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class WidgetProvider extends AppWidgetProvider {
    private static final String SYNC_CLICKED = "automaticWidgetSyncButtonClick";
    private static final String HOURLY_CLICKED = "hourlyWidgetButtonClicked";
    private static final String DAILY_CLICKED = "dailyWidgetButtonClicked";
    private static final String WITHOUT_CLICKED = "withoutWidgetButtonClicked";
    private static String cityNameWidget = "London";
    private static boolean DAILY_BOOL = false;
    private static boolean HOURLY_BOOL = false;
    private static boolean WITHOUT_BOOL = false;
    public static GetWeatherInfoAPI api;
    public static ForeCastAPI forecastApi;
    public static RemoteViews remoteViews;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ComponentName watchWidget;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main_layout);
        watchWidget = new ComponentName(context, WidgetProvider.class);
        if (cityNameWidget.equals("")) {
            remoteViews.setViewVisibility(R.id.widget_add_city, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget, View.GONE);
        } else {
            api = new GetWeatherInfoAPI(cityNameWidget);
            forecastApi = new ForeCastAPI(cityNameWidget);
            waitForApi(api, forecastApi, remoteViews, watchWidget, appWidgetManager, context);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews;
            ComponentName watchWidget;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main_layout);
            watchWidget = new ComponentName(context, WidgetProvider.class);
            api = new GetWeatherInfoAPI(cityNameWidget);
            waitForApi(api, forecastApi, remoteViews, watchWidget, appWidgetManager, context);
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
        if (HOURLY_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews;
            ComponentName watchWidget;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main_layout);
            watchWidget = new ComponentName(context, WidgetProvider.class);

            if (HOURLY_BOOL) {
                HOURLY_BOOL = false;
                remoteViews.setTextColor(R.id.widget_hourly_button, ContextCompat.getColor(context, R.color.colorGrayLight));
                remoteViews.setViewVisibility(R.id.widget_hourly_forecast2, View.GONE);
            } else {
                HOURLY_BOOL = true;
                remoteViews.setTextColor(R.id.widget_hourly_button, ContextCompat.getColor(context, R.color.colorPrimary));
                remoteViews.setViewVisibility(R.id.widget_hourly_forecast2, View.VISIBLE);
            }

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
        if (DAILY_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews;
            ComponentName watchWidget;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main_layout);
            watchWidget = new ComponentName(context, WidgetProvider.class);

            if (DAILY_BOOL) {
                DAILY_BOOL = false;
                remoteViews.setTextColor(R.id.widget_daily_button, ContextCompat.getColor(context, R.color.colorGrayLight));
                remoteViews.setViewVisibility(R.id.widget_daily_forecast2, View.GONE);
            } else {
                DAILY_BOOL = true;
                remoteViews.setTextColor(R.id.widget_daily_button, ContextCompat.getColor(context, R.color.colorPrimary));
                remoteViews.setViewVisibility(R.id.widget_daily_forecast2, View.VISIBLE);
            }

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
        if (WITHOUT_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews;
            ComponentName watchWidget;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main_layout);
            watchWidget = new ComponentName(context, WidgetProvider.class);

            if (WITHOUT_BOOL) {
                WITHOUT_BOOL = false;
                remoteViews.setTextColor(R.id.widget_without_button, ContextCompat.getColor(context, R.color.colorPrimary));
                remoteViews.setViewVisibility(R.id.widget_hourly_forecast2, View.GONE);
                remoteViews.setViewVisibility(R.id.widget_daily_forecast2, View.GONE);
            } else {
                WITHOUT_BOOL = true;
                remoteViews.setTextColor(R.id.widget_without_button, ContextCompat.getColor(context, R.color.colorGrayLight));
                remoteViews.setViewVisibility(R.id.widget_hourly_forecast2, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.widget_daily_forecast2, View.VISIBLE);
            }

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void waitForApi(final GetWeatherInfoAPI api, final ForeCastAPI forecastApi ,final RemoteViews remoteViews, final ComponentName watchWidget, final AppWidgetManager appWidgetManager, final Context context){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (api.success ) {
                    onFinish(api, forecastApi, remoteViews, watchWidget, appWidgetManager, context);
                } else {
                    waitForApi(api, forecastApi,remoteViews, watchWidget, appWidgetManager,context);
                }
            }
        }, 100);
    }

    public void onFinish(GetWeatherInfoAPI api, ForeCastAPI forecastApi, RemoteViews remoteViews, ComponentName watchWidget, AppWidgetManager appWidgetManager, Context context){

        String minMaxWidget = String.format("H: %s° / L: %s°", api.getMaxTemp(), api.getMinTemp());
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEE, dd. MMM", Locale.ENGLISH);//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        String date = String.format("%s | %s", strDate, api.getCountry());
        remoteViews.setTextViewText(R.id.widget_city_name, api.getCityName());
        remoteViews.setImageViewResource(R.id.widget_image, api.getIcon());
        remoteViews.setTextViewText(R.id.widget_temp_main, api.getMainTemp());
        remoteViews.setTextViewText(R.id.widget_max_min, minMaxWidget);
        remoteViews.setTextViewText(R.id.widget_time_date, date);



        Log.i("NEJC REMOTEVIEW",api.getCityName());

        remoteViews.setOnClickPendingIntent(R.id.widget_image, getPendingSelfIntent(context, SYNC_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_hourly_button, getPendingSelfIntent(context, HOURLY_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_daily_button, getPendingSelfIntent(context, DAILY_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_without_button, getPendingSelfIntent(context, WITHOUT_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);

    }

}

