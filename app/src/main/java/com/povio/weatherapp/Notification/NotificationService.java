package com.povio.weatherapp.Notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.povio.weatherapp.MainActivity;
import com.povio.weatherapp.R;

public class NotificationService extends IntentService implements DialogInterface.OnClickListener{


    public NotificationService(){
        super("notification service");
    }

    public NotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        showNotification();
        String dataString = intent.getDataString();
        //Do work here based on dataString
    }

    private void showNotification() {

        Uri soundUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("12° in Medvode")
                .setContentText("Partly cloud")
                .setContentIntent(
                        PendingIntent.getActivity(this, 0, new Intent(this,
                                        MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setSound(soundUri).setSmallIcon(R.drawable.ic)
                .build();
        NotificationManagerCompat.from(this).notify(0, notification);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
