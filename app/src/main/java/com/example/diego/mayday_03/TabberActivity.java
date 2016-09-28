package com.example.diego.mayday_03;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;



/**
 * Created by jorge on 22/09/16.
 * This activity handles all the content in the App by switching the tabs and rendering
 * its corresponding view
 */

public class TabberActivity extends AppCompatActivity {

    private String log_v = "ContactFragment";

    /**Login server credentials and background thread to listen incoming requests**/
    private String mayDayId;
    private String password;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tabber);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        setTabSelectorSettings(tabLayout);
        setPagerConfig(tabLayout);

        initializeServerConnection();
    }

    private void initializeServerConnection() {
        mayDayId = getIntent().getExtras().getString("mayDayID");
        password = getIntent().getExtras().getString("password");

        app = (MyApplication) getApplication();
        app.setCredentials(mayDayId, password);

        app.connect();
        app.startListening();
    }

    private void setPagerConfig(TabLayout tabLayout) {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

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