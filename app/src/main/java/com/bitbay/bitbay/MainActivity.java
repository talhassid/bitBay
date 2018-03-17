package com.bitbay.bitbay;

import android.support.v4.view.ViewPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //fragment manger part
    private SectionsStatePagerAdaptor mSectionsStatePagerAdaptor;
    private ViewPager mViewPager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsStatePagerAdaptor = new SectionsStatePagerAdaptor(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        findViewById(R.id.info_button).setOnClickListener(this); //get info button
        findViewById(R.id.rate_button).setOnClickListener(this); //bitcoin rate button frag
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdaptor adapter = new SectionsStatePagerAdaptor(getSupportFragmentManager());
        adapter.addFragment(new MainFragment() ,"MainFragment");
        adapter.addFragment(new BitCoinRate() ,"BitCoinRate");
        adapter.addFragment(new RateFragment2(),"RateFragment2");

        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rate_button:
                bitRate_frag();
//                setViewPager(0);
                break;
            case R.id.info_button:
                infoButton();
                break;
            // ...
        }
    }

    private void infoButton() {
        Intent push = new Intent(MainActivity.this,Main2info.class);
        startActivity(push);
    }

    private  void bitRate_frag() {
        BitCoinRate bit_rate_frag = new BitCoinRate();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.txtHello,bit_rate_frag,"firstFrag");
        transaction.commit();
   }
}
