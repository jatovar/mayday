package com.spadigital.mayday.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Models.DataBaseHelper;
import com.spadigital.mayday.app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jorge on 27/09/16.
 */
public class ConfigFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    public final static String PREF_KEY_DESTROY_RECEIPT  = "pref_key_destroy_receipt";
    public final static String PREF_KEY_DESTROY_MINE     = "pref_key_destroy_mine";
    public final static String PREF_KEY_BLOCKED_CONTACTS = "pref_key_blocked_contacts";


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
       // MultiSelectListPreferenceDialogFragmentCompat
    }
    @Override public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference destroyReceipt = findPreference(PREF_KEY_DESTROY_RECEIPT);
        destroyReceipt.setSummary(((ListPreference)destroyReceipt).getEntry());

        Preference destroyMine = findPreference(PREF_KEY_DESTROY_MINE);
        destroyMine.setSummary(((ListPreference)destroyMine).getEntry());

        final MultiSelectListPreference blockedContacts = (MultiSelectListPreference)findPreference(PREF_KEY_BLOCKED_CONTACTS);
        blockedContacts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setBlockedContactsList(blockedContacts);
                return false;
            }
        });
        blockedContacts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ArrayList<String>checkedFields = new ArrayList<>();
                checkedFields.addAll((Set<String>)newValue);
                DataBaseHelper db = new DataBaseHelper(ConfigFragment.this.getContext());
                for (String checkedField : checkedFields) {
                    db.unblockContact(checkedField);
                }
                db.close();

                return true;
            }
        });
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
                setRingtonePreferenceValue(ringtone.toString()); // TODO
            } else {
                // "Silent" was selected
                setRingtonePreferenceValue(""); // TODO
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setRingtonePreferenceValue(String s) {
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
                destroyMine.setSummary(((ListPreference)destroyMine).getEntry());
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

    private void setBlockedContactsList(MultiSelectListPreference lp) {

        DataBaseHelper db = new DataBaseHelper(this.getContext());
        ArrayList<Contact> blockedContacts = db.getBlockedContacts();
        db.close();
        List<String> entriesList = new ArrayList<>();
        List<String> entryValuesList = new ArrayList<>();

        for (int i = 0; i < blockedContacts.size(); i++) {
            entriesList.add(blockedContacts.get(i).getMayDayId());
            entryValuesList.add(blockedContacts.get(i).getMayDayId());
        }

        CharSequence[] entries = entriesList.toArray(new CharSequence[entriesList.size()]);
        CharSequence[] entryValues = entryValuesList.toArray(new CharSequence[entryValuesList.size()]);

        lp.setDefaultValue(entryValues);
        lp.setEntries(entries);
        lp.setEntryValues(entryValues);
        lp.setDialogTitle("Selecciona el contacto que deseas desbloquear");
        lp.setPositiveButtonText("Ok");
        lp.setNegativeButtonText("Cancelar");

    }


}