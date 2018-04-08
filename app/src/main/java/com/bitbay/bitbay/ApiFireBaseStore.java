package com.bitbay.bitbay;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.internal.zzebv;
import com.google.android.gms.internal.zzeih;
import com.google.android.gms.internal.zzeio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by roeis on 3/22/2018.
 */

public class ApiFireBaseStore {

    static void addItem2DataBase(DatabaseReference mDataBase,StoreItem item){

        DatabaseReference mUserRef = mDataBase.child("users");
        DatabaseReference mItemRef =mDataBase.child("items");

        String itemKey = mItemRef.push().getKey();
        item.setItemKey(itemKey);
        mItemRef.child(itemKey).child("item").setValue(item.getItemKey());
        mItemRef.child(itemKey).child("userId").setValue(item.getSellerKey());
        mItemRef.child(itemKey).child("categories").setValue(item.getCategoryList());
        mItemRef.child(itemKey).child("storagePath").setValue(item.getImagePath());
        mItemRef.child(itemKey).child("price").setValue(item.getPrice());
        mItemRef.child(itemKey).child("description").setValue(item.getDescription());
        mUserRef.child(item.getSellerKey()).child("items").child(itemKey).setValue(itemKey);


    }

    static void readDataOnce(DatabaseReference mDataBaseRef, GoogleSignInAccount mAccount,
                                    final long[] val,final OnGetDataListener listener){

        String accountId = mAccount.getId();
        mDataBaseRef.child("users").child(accountId).child("items").addValueEventListener(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               listener.onSuccess(dataSnapshot);
//               showData(dataSnapshot,val);
                            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        return ;

    }

    void removeItemFromDatebase (DatabaseReference mDataBase,StoreItem item){

//        DatabaseReference mUserRef = mDataBase.child("users");
//        DatabaseReference mItemRef = mDataBase.child("items");
//        String itemKey = item.getItemKey();
//        item.setItemKey(itemKey);
//
//        mItemRef.child(itemKey).child("cartWatchers");



    }

    static void addItem2Cart(DatabaseReference mDataBase,StoreItem item,
                             GoogleSignInAccount cartAccount){

        DatabaseReference mUserRef = mDataBase.child("users");
        DatabaseReference mItemRef = mDataBase.child("items");
        String itemKey = item.getItemKey();
        item.setItemKey(itemKey);

        mItemRef.child(itemKey).child("cartWatchers").child(cartAccount.getId())
                .setValue(cartAccount.getId());
        mUserRef.child(cartAccount.getId()).child("cart").child(itemKey).setValue(itemKey);

    }
    static void removeItemFromCart(DatabaseReference mDataBase,StoreItem item,
                                   GoogleSignInAccount cartAccount){
        DatabaseReference mUserRef = mDataBase.child("users");
        DatabaseReference mItemRef = mDataBase.child("items");
        String itemKey = item.getItemKey();
        item.setItemKey(itemKey);

        mItemRef.child(itemKey).child("cartWatchers").child(cartAccount.getId()).removeValue();
        mUserRef.child(cartAccount.getId()).child("cart").child(itemKey).removeValue();
    }

}
