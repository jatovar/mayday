package com.spadigital.mayday.app.Helpers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;

import com.spadigital.mayday.app.MayDayApplication;

import java.util.HashMap;

/**
 * Created by jorgetovar on 22/12/16.
 */

public class AlertsHelper {
    private static class AlertReceiver extends BroadcastReceiver {

        private static HashMap<Activity, AlertReceiver> registrations;
        private Context activityContext;

        static {
            registrations = new HashMap<Activity, AlertReceiver>();
        }

        static void register(Activity activity) {
            AlertReceiver receiver = new AlertReceiver(activity);
            activity.registerReceiver(receiver, new IntentFilter(MayDayApplication.INTENT_DISPLAYERROR));
            registrations.put(activity, receiver);
        }

        static void unregister(Activity activity) {
            AlertReceiver receiver = registrations.get(activity);
            if(receiver != null) {
                activity.unregisterReceiver(receiver);
                registrations.remove(activity);
            }
        }

        private AlertReceiver(Activity activity) {
            activityContext = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            String msg = intent.getStringExtra(Intent.EXTRA_TEXT);
            displayErrorInternal(activityContext, msg);
        }
    }

    public static void register(Activity activity) {
        AlertReceiver.register(activity);
    }

    public static void unregister(Activity activity) {
        AlertReceiver.unregister(activity);
    }

    public static void displayError(Context context, String msg) {
        Intent intent = new Intent(MayDayApplication.INTENT_DISPLAYERROR);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        context.sendOrderedBroadcast(intent, null);
    }

    private static void displayErrorInternal(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();

        alert.show();
    }

}
