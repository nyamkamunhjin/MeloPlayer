package com.munhj.meloplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

import Fragments.ListFragment;
import Fragments.musicFragment;
import Model.ListItem;
import MusicHandler.MyMusicHandler;

public class MainActivity extends AppCompatActivity {
    public static final int STORAGE_PERMISSION = 1;
    String requiredPermission = Manifest.permission.READ_EXTERNAL_STORAGE;

    private static List<ListItem> listItems;
    public static ListItem item;
    public static ListItem savedItem;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ask for reading storage permission
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        try {
            int checkValue = this.checkCallingOrSelfPermission(requiredPermission);

            if (checkValue == PackageManager.PERMISSION_GRANTED) {
                Log.d("", "granted");
                ;
                listItems = new MyMusicHandler(this).scanWithMediaProvider(0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        if (!MusicNotification.isIsBackFromNotification()) {
            bottomNav.setSelectedItemId(R.id.nav_music_list);
            MusicNotification.setIsBackFromNotification(true);
        } else
            bottomNav.setSelectedItemId(R.id.nav_music_player);


    }

    // bottom navigation listener
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_music_list:
                            ListFragment listFragment = new ListFragment();
                            listFragment.setBottomNavigationView(bottomNav);
                            selectedFragment = listFragment;
                            break;
                        case R.id.nav_music_player:
                            musicFragment musicFragment = new musicFragment();
                            musicFragment.setBottomNavigationView(bottomNav);
                            selectedFragment = musicFragment;
                            break;
                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
            };

    public static List<ListItem> getListItems() {
        return listItems;
    }

}
