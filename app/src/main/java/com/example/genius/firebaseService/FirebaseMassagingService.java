package com.example.genius.firebaseService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.genius.Activity.MainActivity;
import com.example.genius.R;
import com.example.genius.helper.Preferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMassagingService extends FirebaseMessagingService {

    Integer notificationId = 0;
    Intent resultIntent;
    TaskStackBuilder stackBuilder;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getData().get("title");
        String msg = remoteMessage.getData().get("message");
        String page = remoteMessage.getData().get("page");

        addNotification(title, msg, page);
    }

    private Integer incrementNotificationId() {
        return notificationId++;
    }

    public void addNotification( String title,String body, String page) {

        String CHANNEL_ID = "MasterMind";
        CharSequence name = "MasterMind_Channel";
        String Description = "MasterMind";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setShowBadge(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        Intent intentAction3 = new Intent(this, NotificationReceiver.class);
        intentAction3.putExtra("Action", page);
        intentAction3.putExtra("NotificationID",notificationId);

        Preferences.getInstance(this).setString(Preferences.KEY_FIRE_NOTIFICATION,"notification");

        resultIntent = new Intent(this, MainActivity.class);
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setSound(notificationsound)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setColor(getResources().getColor(R.color.purple_200))
                .addAction(R.mipmap.ic_launcher, "View", resultPendingIntent);

        if (notificationManager != null) {

            notificationManager.notify(incrementNotificationId(), builder.build());
            notificationManager.cancel(incrementNotificationId());
        }
    }
}
