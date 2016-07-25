package com.povio.weatherapp.Widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

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
    private static final String CHANGE_CITY = "changeCityName";
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

        if (CHANGE_CITY.equals(intent.getAction())){
            //alertEditText(context);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Intent intent2 = new Intent(context, WidgetChangeCity.class);
            ComponentName watchWidget;
            watchWidget = new ComponentName(context, WidgetProvider.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main_layout);
            remoteViews.setOnClickPendingIntent(R.id.widget_change_city, pendingIntent);

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }

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

    private void waitForApi(final GetWeatherInfoAPI api, final ForeCastAPI forecastApi, final RemoteViews remoteViews, final ComponentName watchWidget, final AppWidgetManager appWidgetManager, final Context context) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (api.success && forecastApi.success) { //nul pointer exception
                    onFinish(api, forecastApi, remoteViews, watchWidget, appWidgetManager, context);
                } else {
                    waitForApi(api, forecastApi, remoteViews, watchWidget, appWidgetManager, context);
                }
            }
        }, 100);
    }

    public void onFinish(GetWeatherInfoAPI api, ForeCastAPI forecastApi, RemoteViews remoteViews, ComponentName watchWidget, AppWidgetManager appWidgetManager, Context context) {

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

        try {
            for (int i = 0; i < 6; i++) {
                int widgetHourlyTimeId = context.getResources().getIdentifier("widget_hourly_forecast_time" + (i + 1), "id", context.getPackageName());
                int widgetHourlyIconId = context.getResources().getIdentifier("widget_hourly_forecast_icon" + (i + 1), "id", context.getPackageName());
                int widgetHourlyTempId = context.getResources().getIdentifier("widget_hourly_forecast_temp" + (i + 1), "id", context.getPackageName());

                remoteViews.setTextViewText(widgetHourlyTimeId, forecastApi.getTimeL().get(i));
                remoteViews.setImageViewResource(widgetHourlyIconId, forecastApi.getIconL().get(i));
                remoteViews.setTextViewText(widgetHourlyTempId, forecastApi.getMainTempL().get(i));

            }
        } catch (Exception e) {
            Log.v("FORECASTAPIHOURLY", e.getMessage() + "");
        }


        try {
            int i = 0, j = 0;
            for (String elt : forecastApi.getTimeL()) {
                if (elt.equals("15:00")) {

                    int widgetForecastDayId = context.getResources().getIdentifier("widget_daily_forecast_day" + (i + 1), "id", context.getPackageName());
                    int widgetForecastIconId = context.getResources().getIdentifier("widget_daily_forecast_icon" + (i + 1), "id", context.getPackageName());
                    int widgetForecastTempId = context.getResources().getIdentifier("widget_daily_forecast_temp" + (i + 1), "id", context.getPackageName());

                    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                    Date MyDate = newDateFormat.parse(forecastApi.getDateAndTimeL().get(j));
                    newDateFormat.applyPattern("EEEE");
                    String nameOfDay = newDateFormat.format(MyDate);
                    remoteViews.setTextViewText(widgetForecastDayId, nameOfDay);

                    String tempString = String.format("%s°", forecastApi.getMainTempL().get(i));
                    remoteViews.setTextViewText(widgetForecastTempId, tempString);

                    remoteViews.setImageViewResource(widgetForecastIconId, forecastApi.getIconL().get(i));
                    i++;

                }
                j++;
            }
        } catch (Exception e) {
            Log.v("FORECASTAPI", e.toString() + "");
        }


        Log.i("NEJC REMOTEVIEW", api.getCityName());

        remoteViews.setOnClickPendingIntent(R.id.widget_image, getPendingSelfIntent(context, SYNC_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_hourly_button, getPendingSelfIntent(context, HOURLY_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_daily_button, getPendingSelfIntent(context, DAILY_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_without_button, getPendingSelfIntent(context, WITHOUT_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_change_city, getPendingSelfIntent(context, CHANGE_CITY));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);

    }
    public void alertEditText(final Context context){
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText input = new EditText(context);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString().trim();
                Toast.makeText(context.getApplicationContext(), value,
                        Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        alert.show();
    }
}

