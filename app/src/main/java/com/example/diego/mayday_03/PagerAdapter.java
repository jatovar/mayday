package com.example.diego.mayday_03;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by jorge on 22/09/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter{
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

      switch (position) {
            case 0:
                ContactsFragment tab1 = new ContactsFragment();
                System.out.println("T0");
                return tab1;
            case 1:
                ContactsFragment tab2 = new ContactsFragment();
                System.out.println("T1");
                return tab2;
            case 2:
                ContactsFragment tab3 = new ContactsFragment();
                System.out.println("T2");
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
