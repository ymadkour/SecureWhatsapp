package com.example.securewhatsapp.app;

import java.util.ArrayList;
import java.util.Locale;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TabHost;
import android.widget.TextView;

import android.widget.TabHost.TabSpec;

import org.json.JSONObject;

public class HomePage extends TabActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Resources ressources = getResources();
        TabHost tabHost = getTabHost();


        // Apple tab
        Intent intentPhContacts = new Intent().setClass(this, ContactListActivity.class);
        TabSpec tabSpecPhContacts = tabHost
                .newTabSpec("Phone Contacts")
                .setIndicator("Contacts", null)
                .setContent(intentPhContacts);

        // Windows tab
        Intent intentFriends = new Intent().setClass(this, FriendsListActivity.class);
        TabSpec tabSpecFriends = tabHost
                .newTabSpec("Friends")
                .setIndicator("Friends", null)
                .setContent(intentFriends);

        // Blackberry tab
        Intent intentChat = new Intent().setClass(this, ChatListActivity.class);
        TabSpec tabSpecChat = tabHost
                .newTabSpec("Chat")
                .setIndicator("Chat", null)
                .setContent(intentChat);

        // add all tabs

        tabHost.addTab(tabSpecPhContacts);
        tabHost.addTab(tabSpecFriends);
        tabHost.addTab(tabSpecChat);

        //set Windows tab as default (zero based)
        tabHost.setCurrentTab(2);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_next) {
            Intent nextScreen = new Intent(getApplicationContext(), PendingRequestList.class);
            //Sending data to another Activity
            startActivity(nextScreen);
            return true;
        }
        return super.onOptionsItemSelected(item);



    }



}

