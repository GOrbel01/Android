package courserra.gorbel01.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Cloud on 23/08/2015.
 */
public class TakeSelfieAlarmNotificationReceiver extends BroadcastReceiver {
    private static final int MY_NOTIFICATION_ID = 1;
    private static final String TAG = "SelfieNotificationReceiver";

    private final CharSequence tickerText = "Take your Selfie you damn fool!";
    private final CharSequence contentTitle = "Reminder!";
    private final CharSequence takeSelfieText = "Time to take a selfie!";

    private Intent mNotifyIntent;
    private PendingIntent mContentIntent;

    public void onReceive(Context context, Intent intent) {
        mNotifyIntent = new Intent(context, SelfieManagerActivity.class);

        mContentIntent = PendingIntent.getActivity(context, 0, mNotifyIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setTicker(tickerText)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true).setContentTitle(contentTitle)
                .setContentText(takeSelfieText).setContentIntent(mContentIntent);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());

        Log.i(TAG, "Sending Notification at:" + DateFormat.getDateTimeInstance().format(new Date()));
    }
}
