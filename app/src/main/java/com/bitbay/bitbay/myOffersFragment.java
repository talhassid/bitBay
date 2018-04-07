package com.bitbay.bitbay;


import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class myOffersFragment extends Fragment {

    private ProfileActivity activity;
    private ListView mItemListView;
    private ArrayList<StoreItem> mItemsArrayList = new ArrayList<>();

    public myOffersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_offers, container, false);

        activity = (ProfileActivity) getActivity();
        mItemListView = rootView.findViewById(R.id.items_list);

        final CustomListAdapter customListAdapter = new CustomListAdapter(
                activity,R.layout.custom_items_list_view,mItemsArrayList);

        mItemListView.setAdapter(customListAdapter);

        activity.mDatabaseRef.child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String userKey= String.valueOf(dataSnapshot.child("userId").getValue());
                if(userKey.equals(activity.getMyAccount().getId())) {
                    String price = String.valueOf(dataSnapshot.child("price").getValue());
                    String imagePath = String.valueOf(dataSnapshot.child("storagePath").getValue());
                    String description = String.valueOf(dataSnapshot.child("description").getValue());
                    String categories = (String) dataSnapshot.child("categories").getValue();
                    Log.i("**price**", price);
                    Log.i("**imagePath**", imagePath);
                    Log.i("**description**", description);

                    StoreItem item = new StoreItem(price,description,imagePath,userKey,categories);
                    (mItemsArrayList).add(item);

                }
                customListAdapter.notifyDataSetChanged();
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

        return rootView;
    }

}






