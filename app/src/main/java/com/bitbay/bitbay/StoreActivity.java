package com.bitbay.bitbay;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StoreActivity extends AppCompatActivity implements View.OnClickListener{

    protected String[] listCategories;
    protected String filter;
    protected GoogleSignInAccount myAccount = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        myAccount = (GoogleSignInAccount)bundle.get("account");

        listCategories = getResources().getStringArray(R.array.category_list);

        Button category_1 = (Button) findViewById(R.id.category_1);
        category_1.setOnClickListener(this); // calling onClick() method
        Button category_2 = (Button) findViewById(R.id.category_2);
        category_2.setOnClickListener(this);
        Button category_3 = (Button) findViewById(R.id.category_3);
        category_3.setOnClickListener(this);
        Button category_4 = (Button) findViewById(R.id.category_4);
        category_4.setOnClickListener(this); // calling onClick() method
        Button category_5 = (Button) findViewById(R.id.category_5);
        category_5.setOnClickListener(this);
        Button category_6 = (Button) findViewById(R.id.category_6);
        category_6.setOnClickListener(this);
        Button category_7 = (Button) findViewById(R.id.category_7);
        category_7.setOnClickListener(this); // calling onClick() method
        Button category_8 = (Button) findViewById(R.id.category_8);
        category_8.setOnClickListener(this);
        Button category_9 = (Button) findViewById(R.id.category_9);
        category_9.setOnClickListener(this);




    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.category_1:
//                setTitle("New items");
                filter = "New items";
                break;

            case R.id.category_2:
//                setTitle("Fashion");
                filter = "Fashion";

                break;

            case R.id.category_3:
//                setTitle("Electronics");
                filter = "Electronics";
                break;

            case R.id.category_4:
//                setTitle("Toys");
                filter = "Toys";
                break;

            case R.id.category_5:
//                setTitle("Sporting Goods");
                filter = "Sporting Goods";
                break;

            case R.id.category_6:
//                setTitle("Home and Garden");
                filter = "Home and Garden";

                break;

            case R.id.category_7:
//                setTitle("Music");
                filter = "Music";
                break;

            case R.id.category_8:
//                setTitle("Business and Industrial");
                filter = "Business and Industrial";
                break;

            case R.id.category_9:
//                setTitle("Motors");
                filter = "Motors";
                break;

            default:
                filter = "nothing";
                break;
        }

        Intent myIntent = new Intent(StoreActivity.this, ActivityFilteredCategories.class);
        myIntent.putExtra("filter",filter);
        myIntent.putExtra("account",myAccount);
        startActivity(myIntent);
    }




}

