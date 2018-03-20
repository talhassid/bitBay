package com.bitbay.bitbay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


public class LoggedInActivity extends AppCompatActivity {

    private GoogleSignInAccount myAccount = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        myAccount = (GoogleSignInAccount)bundle.get("account");
        setContentView(R.layout.activity_logged_in);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Log.e("temp-to_implement", "help button pressed");
                return true;
            case R.id.shop:
                Log.e("temp-to_implement", "shop button pressed");
                return true;
            case R.id.profile:
                profile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private  void profile() {
        Intent profileIntent = new Intent(LoggedInActivity.this, ProfileActivity.class);
        profileIntent.putExtra("account",myAccount);
        startActivity(profileIntent);
    }

}