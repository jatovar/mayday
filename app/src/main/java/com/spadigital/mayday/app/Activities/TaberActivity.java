package com.spadigital.mayday.app.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private Toolbar myToolbar;
    private static TaberActivity instance;

    public static TaberActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabber);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        myToolbar = (Toolbar)findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);
        myToolbar.setPadding(100, 0,  0, 0);
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setTitle("Mensajes");
        setTabSelectorSettings(tabLayout);
        setPagerConfig(tabLayout);
        instance = this;


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


                switch (tab.getPosition()){
                    case 0:
                        myToolbar.setTitle("Mensajes");
                        break;
                    case 1:
                        myToolbar.setTitle("Contactos");
                        break;
                    case 2:
                        myToolbar.setTitle("Configuración");

                        break;
                }


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
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_tab_messages);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));

        View view2 = getLayoutInflater().inflate(R.layout.item_tab, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_tab_contacts);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        View view3 = getLayoutInflater().inflate(R.layout.item_tab, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_tab_settings);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }
}
