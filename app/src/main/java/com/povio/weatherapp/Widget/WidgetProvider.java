package com.povio.weatherapp.Widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.povio.weatherapp.CityQuery;
import com.povio.weatherapp.GetWeatherInfoAPI;
import com.povio.weatherapp.R;

import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class WidgetProvider extends AppWidgetProvider{
    public static String ACTION_WIDGET_CLICK = "klik";
    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
        Log.d("nejcTest", "onRecive");
        Toast.makeText(context, "onReceive", Toast.LENGTH_SHORT).show();
        if (ACTION_WIDGET_CLICK.equals(intent.getAction())){
            Log.d("nejcTest", "onRecive");
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID: ids) {
                updateAppWidget(context, appWidgetManager, appWidgetID);

            }
      }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final  int nb = appWidgetIds.length;
        for (int i = 0; i < nb; i++) {
            int appWidgetId = appWidgetIds[i];
            Log.d("nejcTest", "onUpdate");
            Toast.makeText(context, "onUpdate", Toast.LENGTH_SHORT).show();
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction(ACTION_WIDGET_CLICK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,	intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widget_daily_button, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            // Tell the AppWidgetManager to perform an update on the current app
            // widget



            // Update The clock label using a shared method
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    public static void updateAppWidget(Context context,	AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews updateViews = new RemoteViews(context.getPackageName(),	R.layout.widget_layout);
        updateViews.setTextViewText(R.id.widget_city_name, new Random().nextInt(10)+"");
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);
    }

}
