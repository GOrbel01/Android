package courserra.gorbel01.dailyselfie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Cloud on 23/08/2015.
 */
public class SelfieAlarmLoggerReceiver extends BroadcastReceiver {
    private static final String TAG = "SelfieAlarmLoggerReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Logging alarm at:" + DateFormat.getDateTimeInstance().format(new Date()));
    }
}
