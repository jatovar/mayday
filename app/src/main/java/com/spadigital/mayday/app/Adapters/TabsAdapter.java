package com.spadigital.mayday.app.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.spadigital.mayday.app.Fragments.ConfigFragment;
import com.spadigital.mayday.app.Fragments.ContactsFragment;
import com.spadigital.mayday.app.Fragments.ConversationsFragment;

/**
 * Created by jorge on 22/09/16.
 */
public class TabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabsAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ContactsFragment tab1 = new ContactsFragment();
                return tab1;
            case 1:
                ConversationsFragment tab2 = new ConversationsFragment();
                return tab2;
            case 2:
                ConfigFragment tab3 = new ConfigFragment();
                return tab3;
            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}