package com.example.diego.mayday_03;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jorge on 27/09/16.
 */
public class ConfigFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    public final static String PREF_KEY_DESTROY_RECEIPT = "pref_key_destroy_receipt";
    public final static String PREF_KEY_DESTROY_MINE    = "pref_key_destroy_mine";


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

    }
    @Override public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preference destroyReceipt = findPreference(PREF_KEY_DESTROY_RECEIPT);
        destroyReceipt.setSummary(((ListPreference)destroyReceipt).getEntry());
    }
    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals("KEY_RINGTONE_PREFERENCE")) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

            String existingValue = getRingtonePreferenceValue(); // TODO
            if (existingValue != null) {
                if (existingValue.length() == 0) {
                    // Select "Silent"
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                } else {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
                }
            } else {
                // No ringtone has been selected, set to the default
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
            }

            startActivityForResult(intent, 333);
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 333 && data != null) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtone != null) {
                setRingtonPreferenceValue(ringtone.toString()); // TODO
            } else {
                // "Silent" was selected
                setRingtonPreferenceValue(""); // TODO
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setRingtonPreferenceValue(String s) {
    }
    private String getRingtonePreferenceValue(){
        return "";
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            case PREF_KEY_DESTROY_RECEIPT:
                Preference destroyReceipt = findPreference(key);
                // Set summary to be the user-description for the selected entry
                destroyReceipt.setSummary(((ListPreference)destroyReceipt).getEntry());
            break;

            case PREF_KEY_DESTROY_MINE:
                Preference destroyMine = findPreference(key);
                destroyMine.setSummary(sharedPreferences.getString(key, ""));
            break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
