package com.bitbay.bitbay;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

/**
 * Created by roeis on 3/22/2018.
 */

public class ApiFireBaseStore {

    static void addItem2DataBase(DatabaseReference mUserRef, DatabaseReference mItemRef,
                                 StorageReference filePath, GoogleSignInAccount myAccount, String item){

        String itemKey = mItemRef.push().getKey();
        mItemRef.child(itemKey).child("item").setValue(item);
        mItemRef.child(itemKey).child("userId").setValue(myAccount.getId());
        mItemRef.child(itemKey).child("storagePath").setValue(filePath.toString());
        mUserRef.child(myAccount.getId()).child("items").child(itemKey).setValue(itemKey);


    }

    void removeItem(StoreItem item){

    }

}
