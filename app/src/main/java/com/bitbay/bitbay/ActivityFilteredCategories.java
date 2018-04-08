package com.bitbay.bitbay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ActivityFilteredCategories extends Activity {

    protected String myFilter;
    protected GoogleSignInAccount myAccount = null ;
    protected DatabaseReference mDatabaseRef;
    protected ListView mCategoryListView ;
    protected ArrayList<StoreItem> mItemsArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_categories);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        myAccount = (GoogleSignInAccount) bundle.get("account");
        myFilter = (String) bundle.get("filter");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mCategoryListView = this.findViewById(R.id.category_items); //todo: verify it

        final CategoriesListAdapter categoriesListAdapter = new CategoriesListAdapter(
                this, R.layout.custom_items_list_view, mItemsArrayList);

        mCategoryListView.setAdapter(categoriesListAdapter);

        this.mDatabaseRef.child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String userKey = String.valueOf(dataSnapshot.child("userId").getValue());
                if (!userKey.equals(myAccount.getId())) {

                    String price = String.valueOf(dataSnapshot.child("price").getValue());
                    String imagePath = String.valueOf(dataSnapshot.child("storagePath").getValue());
                    String description = String.valueOf(dataSnapshot.child("description").getValue());
                    String categories = (String) dataSnapshot.child("categories").getValue();

                    Log.i("**price**", price);
                    Log.i("**imagePath**", imagePath);
                    Log.i("**description**", description);
                    Log.i("**categories**", categories);

                    if (categories.contains(myFilter)) {
                        Log.i("**inside mt filter**", myFilter);
                        StoreItem item = new StoreItem(price, description, imagePath, userKey, categories);
                        (mItemsArrayList).add(item);
                    }

                }
                categoriesListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
}
