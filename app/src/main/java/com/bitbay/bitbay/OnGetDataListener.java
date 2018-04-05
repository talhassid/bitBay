package com.bitbay.bitbay;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by roeis on 4/5/2018.
 */

public interface OnGetDataListener {
    public void onStart();
    public void onSuccess(DataSnapshot data);
    public void onFailed(DatabaseError databaseError);
}
