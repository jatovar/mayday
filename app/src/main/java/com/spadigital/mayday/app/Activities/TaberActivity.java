package com.spadigital.mayday.app.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.spadigital.mayday.app.Adapters.TabsAdapter;
import com.spadigital.mayday.app.R;

/**
 * Created by jorge on 22/09/16.
 * This activity handles all the content in the App by switching the tabs and rendering
 * its corresponding view
 */

public class TaberActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabber);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        setTabSelectorSettings(tabLayout);
        setPagerConfig(tabLayout);
    }


    private void setPagerConfig(final TabLayout tabLayout) {

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                final InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tabLayout.getWindowToken(), 0);
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setTabSelectorSettings(TabLayout tabLayout) {

        View view1 = getLayoutInflater().inflate(R.layout.item_tab, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.user);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));

        View view2 = getLayoutInflater().inflate(R.layout.item_tab, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.chat);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        View view3 = getLayoutInflater().inflate(R.layout.item_tab, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.config);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }
}
