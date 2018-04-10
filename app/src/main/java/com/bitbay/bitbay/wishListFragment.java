package com.bitbay.bitbay;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzagr.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class wishListFragment extends Fragment {

    private ProfileActivity activity;
    private ListView mItemListView;
    private ArrayList<StoreItem> mItemsArrayList = new ArrayList<>();
    int cartFullPrice;
    public wishListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wish_list, container, false);

        activity = (ProfileActivity) getActivity();
        mItemListView = rootView.findViewById(R.id.items_list);
        Button paymentButton = (Button)rootView.findViewById(R.id.cart_payment_button);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartFullPrice = 0;
                for(int i = 0; i < mItemsArrayList.size(); i++){
                    String s_price = mItemsArrayList.get(i).getPrice();
                    int itemPrice = 0;
                    try{
                        itemPrice = Integer.parseInt(s_price);
                    }
                    catch (Exception e){
                        continue;
                    }
                    cartFullPrice = cartFullPrice + itemPrice;
                }
                Intent paymentIntent = new Intent(getActivity(), PaypalActivity.class);
                paymentIntent.putExtra("price",cartFullPrice);
                startActivity(paymentIntent);
            }
        });

        final UserCartListAdapter userCartListAdapter = new UserCartListAdapter(
                activity,R.layout.cart_items_list_view,mItemsArrayList,
                activity.getMyAccount(),activity.mDatabaseRef);

        mItemListView.setAdapter(userCartListAdapter);

//        final Runnable run = new Runnable() {
//            public void run() {
//                mItemsArrayList.clear();
//                mItemsArrayList.addAll();
//                userCartListAdapter.notifyDataSetChanged();
//                mItemListView.invalidateViews();
//                mItemListView.refreshDrawableState();
//            }
//
//        };

        activity.mDatabaseRef.child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("DEBUG","onChildAdded");

                String userKey= String.valueOf(dataSnapshot.child("userId").getValue());
                if(dataSnapshot.child("cartWatchers").hasChild(userKey)) {
                    String price = String.valueOf(dataSnapshot.child("price").getValue());
                    String imagePath = String.valueOf(dataSnapshot.child("storagePath").getValue());
                    String description = String.valueOf(dataSnapshot.child("description").getValue());
                    String categories = (String) dataSnapshot.child("categories").getValue();
                    String itemKey = (String) dataSnapshot.child("item").getValue();

                    Log.i("**price**", price);
                    Log.i("**imagePath**", imagePath);
                    Log.i("**description**", description);

                    StoreItem item = new StoreItem(price,description,imagePath,userKey,categories);
                    item.setItemKey(itemKey);
                    (mItemsArrayList).add(item);

            }
                userCartListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("DEBUG","onChildChanged");

                userCartListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("DEBUG","onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e("DEBUG","onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DEBUG","onCancelled");
            }


        });
        Log.e("DEBUG","return rootView");
        return rootView;
    }


}