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

    static void addItem2History(DatabaseReference mDataBase,StoreItem item,String userKey){
        DatabaseReference mUserRef = mDataBase.child("users");

        mUserRef.child(userKey).child("history").child(item.getItemKey()).child("storagePath").
                setValue(item.getImagePath());
        mUserRef.child(userKey).child("history").child(item.getItemKey()).child("price").
                setValue(item.getPrice());
        mUserRef.child(userKey).child("history").child(item.getItemKey()).child("description").
                setValue(item.getDescription());
        mUserRef.child(userKey).child("history").child(item.getItemKey()).child("item").
                setValue(item.getItemKey());


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

    static void removeItemFromDatebase (DatabaseReference mDataBase,StoreItem item){

        DatabaseReference mUserRef = mDataBase.child("users");
        DatabaseReference mItemRef = mDataBase.child("items");
        String itemKey = item.getItemKey();

        String itemOwner = item.getSellerKey();



        for (String key : item.getCartWatchersList()){
            removeItemFromCart(mDataBase,item,key);
        }

        mUserRef.child(itemOwner).child("items").child(itemKey).removeValue();
        mItemRef.child(itemKey).removeValue();



        mItemRef.child(itemKey).child("cartWatchers");



    }

    static void addItem2Cart(DatabaseReference mDataBase,StoreItem item,
                             String cartAccountId){

        DatabaseReference mUserRef = mDataBase.child("users");
        DatabaseReference mItemRef = mDataBase.child("items");
        String itemKey = item.getItemKey();
        item.setItemKey(itemKey);

        mItemRef.child(itemKey).child("cartWatchers").child(cartAccountId)
                .setValue(cartAccountId);
        mUserRef.child(cartAccountId).child("cart").child(itemKey).setValue(itemKey);

        item.add2CartWatchers(cartAccountId);
    }
    static void removeItemFromCart(DatabaseReference mDataBase,StoreItem item,
                                   String cartAccountId){
        DatabaseReference mUserRef = mDataBase.child("users");
        DatabaseReference mItemRef = mDataBase.child("items");
        String itemKey = item.getItemKey();
        item.setItemKey(itemKey);

        mItemRef.child(itemKey).child("cartWatchers").child(cartAccountId).removeValue();
        mUserRef.child(cartAccountId).child("cart").child(itemKey).removeValue();

        item.removeFromCartWatchers(cartAccountId);
    }

}
