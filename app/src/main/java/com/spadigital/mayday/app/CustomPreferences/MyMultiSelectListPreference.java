package com.spadigital.mayday.app.CustomPreferences;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog.Builder;


import android.support.v14.preference.MultiSelectListPreference;

import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;

/**
 * Created by jorge on 11/10/16.
 */

public class MyMultiSelectListPreference extends android.support.v14.preference.MultiSelectListPreference implements DialogInterface.OnClickListener {


    public MyMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getDialogMessage();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                //Yes button clicked
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                break;
        }
    }




}
