package com.bitbay.bitbay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.zip.Inflater;


public class LoggedInActivity extends AppCompatActivity {

    private GoogleSignInAccount myAccount = null ;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        myAccount = (GoogleSignInAccount)bundle.get("account");
        setContentView(R.layout.activity_logged_in);
        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.help:
                        helpPage();
                        break;
                    case R.id.shop:
                        Log.e("temp-to_implement", "shop button pressed");
                        break;
                    case R.id.profile:
                        profile();
                        break;
                }
                return true;
            }
        });

    }

    private  void profile() {
        Intent profileIntent = new Intent(LoggedInActivity.this, ProfileActivity.class);
        profileIntent.putExtra("account",myAccount);
        startActivity(profileIntent);
    }

    private void helpPage(){
        Intent helpIntent = new Intent(LoggedInActivity.this, HelpActivity.class);
        helpIntent.putExtra("account",myAccount);
        startActivity(helpIntent);
    }

}