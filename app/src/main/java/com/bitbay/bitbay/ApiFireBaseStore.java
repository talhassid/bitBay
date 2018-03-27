package com.bitbay.bitbay;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

/**
 * Created by roeis on 3/22/2018.
 */

public class ApiFireBaseStore {


    static void addItem2DataBase(DatabaseReference mUserRef, DatabaseReference mItemRef,
                                 StoreItem item){

        String itemKey = mItemRef.push().getKey();
        item.setItemKey(itemKey);
        mItemRef.child(itemKey).child("item").setValue(item.getItemKey());
        mItemRef.child(itemKey).child("userId").setValue(item.getSellerKey());
        mItemRef.child(itemKey).child("storagePath").setValue(item.getImagePath());
        mUserRef.child(item.getSellerKey()).child("items").child(itemKey).setValue(itemKey);


    }

    void removeItem(StoreItem item){

    }

}
