package com.bitbay.bitbay;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {

    private Button uploadButton;
    private ProfileActivity activity;

    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        activity = (ProfileActivity) getActivity();
        uploadButton = view.findViewById(R.id.upload_button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*"); //fixme: change to items

                startActivityForResult(intent,activity.getStorageIntent());

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        requestPerms(android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (requestCode == activity.getStorageIntent() && resultCode == activity.RESULT_OK) {
            Uri uri = data.getData();
            StorageReference filePath =
                    activity.getStorageRefferance().child("items").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(activity, "Upload Done", Toast.LENGTH_LONG).show();
                }
            });

        }
    }


    private boolean hasPermissions(String perm){
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

    private void requestPerms(String perm){
        String[] permissions = new String[]{perm};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,activity.getStorageIntent());
        }

    }

}