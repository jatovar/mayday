package com.spadigital.mayday.app.Tasks;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.spadigital.mayday.app.Activities.ChatActivity;

/**
 * Created by jorge on 13/10/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static Vibrator v;
    public static Ringtone r;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(ChatActivity.getInstance() != null && !ChatActivity.getInstance().hasWindowFocus()) {

            ///wake up
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl  = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TRAININGCOUNTDOWN");
            wl.acquire();
            //ringtone
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            r = RingtoneManager.getRingtone(context, uri);
            r.play();
            //vibration
            v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            long[] pattern = {0, 500, 1000};
            v.vibrate(pattern, 0);
        }
    }
}
