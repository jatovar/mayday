package com.spadigital.mayday.app.Tasks;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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

            AudioManager am;
            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            am.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);

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
            // This example will cause the phone to vibrate "SOS" in Morse Code
            // In Morse Code, "s" = "dot-dot-dot", "o" = "dash-dash-dash"
            // There are pauses to separate dots/dashes, letters, and words
            // The following numbers represent millisecond lengths
            int dot = 200;      // Length of a Morse Code "dot" in milliseconds
            int dash = 500;     // Length of a Morse Code "dash" in milliseconds
            int short_gap = 200;    // Length of Gap Between dots/dashes
            int medium_gap = 500;   // Length of Gap Between Letters
            int long_gap = 1000;    // Length of Gap Between Words
            long[] pattern = {
                    0,  // Start immediately
                    dot, short_gap, dot, short_gap, dot,    // s
                    medium_gap,
                    dash, short_gap, dash, short_gap, dash, // o
                    medium_gap,
                    dot, short_gap, dot, short_gap, dot,    // s
                    long_gap
            };

            v.vibrate(pattern, 0);
        }
    }
}
