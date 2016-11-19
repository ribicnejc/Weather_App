package com.povio.weatherapp.Notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.widget.Toast;

import com.povio.weatherapp.ForeCastAPI;
import com.povio.weatherapp.GetWeatherInfoAPI;
import com.povio.weatherapp.MainActivity;
import com.povio.weatherapp.MoreInfoActivity;
import com.povio.weatherapp.R;

public class NotificationService extends IntentService {


    public NotificationService() {
        super("notification service");
    }

    public NotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String city = "Medvode";
        GetWeatherInfoAPI getWeatherInfoAPI;
        getWeatherInfoAPI = new GetWeatherInfoAPI(city);
        waitForApi(getWeatherInfoAPI);

//        showNotification();
//        String dataString = intent.getDataString();
        //Do work here based on dataString
    }

    private void waitForApi(final GetWeatherInfoAPI api) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (api.success) {
                    if (!api.apiFail){
                        showNotification(api);
                    }
                } else {
                    waitForApi(api);
                }
            }
        }, 100);

        int a = 5;
    }


    private void showNotification(GetWeatherInfoAPI api) {
        String title = api.getMainTemp() + " in " + api.getCityName();
        Uri soundUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(api.getMainDesc())
                .setContentIntent(
                        PendingIntent.getActivity(this, 0, new Intent(this,
                                        MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setSound(soundUri).setSmallIcon(R.drawable.ic)
                .build();
        NotificationManagerCompat.from(this).notify(0, notification);
    }


}
