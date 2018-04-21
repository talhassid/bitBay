package com.bitbay.bitbay;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import static android.support.v4.app.ActivityCompat.requestPermissions;



public class ApiPermissions {

    public static boolean hasPermissions(String perm, Activity activity){
        int res = PackageManager.PERMISSION_GRANTED ;
        String[] permissions = new String[]{perm};

        for (String perms : permissions){
            res = activity.checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    public static void requestPerms(String perm, int requestCode, Activity activity){
        String[] permissions = new String[]{perm};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity.requestPermissions(permissions,requestCode);
        }

    }
}
