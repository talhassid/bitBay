package com.bitbay.bitbay;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class myOrdersFragment extends Fragment {

    private ProfileActivity activity;
    private ListView mHistoryItemListView;
    private ArrayList<StoreItem> mHistoryItemsArrayList = new ArrayList<>();
    private String userId;

    public myOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);


        activity = (ProfileActivity) getActivity();
        mHistoryItemListView = rootView.findViewById(R.id.history_items_list);
        final historyListAdapter historyListAdapter = new historyListAdapter(
                activity,R.layout.custom_items_list_view,mHistoryItemsArrayList,activity.mDatabaseRef);

        userId = activity.getMyAccount().getId();

        mHistoryItemListView.setAdapter(historyListAdapter);
        activity.mDatabaseRef.child("users").child(userId).child("history").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                String price = String.valueOf(dataSnapshot.child("price").getValue());
                String address = String.valueOf(dataSnapshot.child("address").getValue());
                String imagePath = String.valueOf(dataSnapshot.child("storagePath").getValue());
                String description = String.valueOf(dataSnapshot.child("description").getValue());
                String categories = (String) dataSnapshot.child("categories").getValue();
                String itemKey = (String) dataSnapshot.child("item").getValue();

                Log.i("**price**", price);
                Log.i("**imagePath**", imagePath);
                Log.i("**description**", description);

                StoreItem item = new StoreItem(price,description,imagePath,userId,categories,address);
                item.setItemKey(itemKey);

                (mHistoryItemsArrayList).add(item);


                historyListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                historyListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                historyListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                historyListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                historyListAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

}