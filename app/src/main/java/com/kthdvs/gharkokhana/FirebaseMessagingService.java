package com.kthdvs.gharkokhana;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by aayeshs on 2/5/18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    String fromId;
    String type;
    Intent resultIntent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();
        fromId = remoteMessage.getData().get("from_user_id");
        type = remoteMessage.getData().get("score");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body);

        if(type.equals("1")){
            resultIntent = new Intent(this, GetOrderActivity.class);
            resultIntent.putExtra("fromId",fromId);
            resultIntent.putExtra("body",body);
        }else{
            resultIntent = new Intent(this,ConfirmationActivity.class);
            resultIntent.putExtra("body",body);
            resultIntent.putExtra("fromId",fromId);
        }


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this,
                        1,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);


        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
