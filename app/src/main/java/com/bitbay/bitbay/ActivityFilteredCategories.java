package com.bitbay.bitbay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        mCategoryListView = this.findViewById(R.id.category_items);

        Button go2Cart = this.findViewById(R.id.go_to_cart_button);
        go2Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(ActivityFilteredCategories.this, ProfileActivity.class);
                cartIntent.putExtra("account",myAccount);
                cartIntent.putExtra("target","cart");
                startActivity(cartIntent);
            }
        });

        final CategoriesListAdapter categoriesListAdapter = new CategoriesListAdapter(
                this, R.layout.category_items_list_view, mItemsArrayList,myAccount,mDatabaseRef);

        mCategoryListView.setAdapter(categoriesListAdapter);

        this.mDatabaseRef.child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String userKey = String.valueOf(dataSnapshot.child("userId").getValue());
                if (true) { //TODO: replace this with next line - this is for debug
//                if (!userKey.equals(myAccount.getId())) { //TODO: add not

                    String price = String.valueOf(dataSnapshot.child("price").getValue());
                    String imagePath = String.valueOf(dataSnapshot.child("storagePath").getValue());
                    String description = String.valueOf(dataSnapshot.child("description").getValue());
                    String categories = (String) dataSnapshot.child("categories").getValue();
                    String itemKey = (String) dataSnapshot.child("item").getValue();

                    if (categories == null){
                        Log.i("**categories**", "is null :(");
                    }
                    else {
                        Log.i("**categories**", categories);
                        Log.i("*myfilter is:*", myFilter);

                        if (categories.contains(myFilter)) {
                            Log.i("*categories contain it*", myFilter);
                            StoreItem item = new StoreItem(price, description, imagePath, userKey, categories);
                            item.setItemKey(itemKey);
                            (mItemsArrayList).add(item);
                        }
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
