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
        mItemRef.child(itemKey).child("storagePath").setValue(item.getImagePath());
        mItemRef.child(itemKey).child("price").setValue(item.getPrice());
        mItemRef.child(itemKey).child("description").setValue(item.getDescription());
        mUserRef.child(item.getSellerKey()).child("items").child(itemKey).setValue(itemKey);


    }

    public static void readDataOnce(DatabaseReference mDataBaseRef, GoogleSignInAccount mAccount,
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


//        public void showData(DataSnapshot dataSnapshot,final long[] in_val){
//
//            in_val[0] = dataSnapshot.getChildrenCount();
//            listener.onSuccess(dataSnapshot);
//            Log.e("debug-**inside* items", String.valueOf(in_val[0]));
//
//            ArrayList<String> dataArry = new ArrayList<>();
//            dataArry
//
//            }

        });

        return ;

    }

    void removeItem(StoreItem item){

    }

}
