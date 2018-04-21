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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.zip.Inflater;


public class LoggedInActivity extends AppCompatActivity {

    private GoogleSignInAccount myAccount = null ;
    private BottomNavigationView bottomNavigation;
    private TextView nameTextView;
    private Button sellButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        myAccount = (GoogleSignInAccount)bundle.get("account");
        setContentView(R.layout.activity_logged_in);
        String name = myAccount.getDisplayName();

        sellButton = findViewById(R.id.sell_button_id);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profileIntent = new Intent(LoggedInActivity.this, ProfileActivity.class);
                profileIntent.putExtra("target","sell");
                profileIntent.putExtra("account",myAccount);
                startActivity(profileIntent);

            }
        });


        nameTextView = findViewById(R.id.acc_name);
        nameTextView.setText(getString(R.string.id_token_fmt, name));
        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.help:
                        helpPage();
                        break;
                    case R.id.store:
                      //  Log.e("temp-to_implement", "shop button pressed");
                        store();
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
        profileIntent.putExtra("target","info");
        profileIntent.putExtra("account",myAccount);
        startActivity(profileIntent);
    }

    private void store(){
        Intent storeIntent = new Intent(LoggedInActivity.this, StoreActivity.class);
        storeIntent.putExtra("account",myAccount);
        startActivity(storeIntent);
    }

    private void helpPage(){
        Intent helpIntent = new Intent(LoggedInActivity.this, HelpActivity.class);
        helpIntent.putExtra("account",myAccount);
        startActivity(helpIntent);
    }

}