package com.reconizer.loveteller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

/**
 * Created by Andrzej on 2017-08-06.
 */

public class MainActivity extends AppCompatActivity {

    static int color = Color.parseColor("#ffff8800");
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        //.setTheme(R.style.GreenTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
        final ViewPager pager = (ViewPager) findViewById(R.id.container);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        refreshButtonColours(pager.getCurrentItem());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                refreshButtonColours(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ImageButton profileButton = (ImageButton) findViewById(R.id.imageProfileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0,true);
            }
        });
        ImageButton chatButton = (ImageButton) findViewById(R.id.imageChatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1,true);
            }
        });
        ImageButton matchButton = (ImageButton) findViewById(R.id.imageMatchButton);
        matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(2,true);
            }
        });
        ImageButton mapButton = (ImageButton) findViewById(R.id.imageMapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(3,true);
            }
        });

    }

    //Pager stworzony na potrzeby tego jednego activity
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override //jezeli dodajemy element warto pamietac ze nalezy zmienic getCount
        public Fragment getItem(int position) {
            switch(position) {
                case 0: {
                    return MainProfileFragment.newInstance();
                }
                case 1: {
                    return MainChatFragment.newInstance();
                }
                case 2: {
                    return MainMatchFragment.newInstance();
                }
                case 3: {
                    return MainMapFragment.newInstance();
                }
                default: return MainMapFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private void refreshButtonColours(int position){
        ImageButton profileButton = (ImageButton) findViewById(R.id.imageProfileButton);
        ImageButton mapButton = (ImageButton) findViewById(R.id.imageMapButton);
        ImageButton chatButton = (ImageButton) findViewById(R.id.imageChatButton);
        ImageButton matchButton = (ImageButton) findViewById(R.id.imageMatchButton);
        switch(position) {
            case 0: {
                profileButton.setColorFilter(color);
                chatButton.setColorFilter(Color.GRAY);
                matchButton.setColorFilter(Color.GRAY);
                mapButton.setColorFilter(Color.GRAY);
                break;
            }
            case 1: {
                profileButton.setColorFilter(Color.GRAY);
                chatButton.setColorFilter(color);
                matchButton.setColorFilter(Color.GRAY);
                mapButton.setColorFilter(Color.GRAY);
                break;
            }
            case 2: {
                profileButton.setColorFilter(Color.GRAY);
                chatButton.setColorFilter(Color.GRAY);
                matchButton.setColorFilter(color);
                mapButton.setColorFilter(Color.GRAY);
                break;
            }
            case 3: {
                profileButton.setColorFilter(Color.GRAY);
                chatButton.setColorFilter(Color.GRAY);
                matchButton.setColorFilter(Color.GRAY);
                mapButton.setColorFilter(color);
            }
        }
    }
}