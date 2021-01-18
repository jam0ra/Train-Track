package com.example.traintrack.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.traintrack.R;
import com.google.android.material.tabs.TabLayout;

public class RosterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roster_main_activity);
        if(savedInstanceState != null){
            // loads the fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_roster,RosterFragment.newInstance(),"tag").commitNow();
        }
    }
}
