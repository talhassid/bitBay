package com.bitbay.bitbay;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {

    private Button uploadButton;
    private ProfileActivity activity;
    private EditText mPrice;
    private EditText mDescription;

    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        activity = (ProfileActivity) getActivity();
        uploadButton = view.findViewById(R.id.upload_button);

        mPrice = view.findViewById(R.id.priceText);
        mDescription = view.findViewById(R.id.descriptionText);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");

                startActivityForResult(intent,activity.getStorageIntent());

            }
        });

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        ApiPermissions.requestPerms(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                activity.getStorageIntent(),activity);

        if (requestCode == activity.getStorageIntent() && resultCode == activity.RESULT_OK) {
            Uri uri = data.getData();
            final StorageReference filePath =
                    activity.getStorageRefferance().child("items").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(activity, "Upload Done", Toast.LENGTH_LONG).show();

                    String price = mPrice.getText().toString();
                    String description = mDescription.getText().toString();

                    StoreItem item = new StoreItem(price,description,taskSnapshot.getDownloadUrl().toString(),
                            activity.myAccount.getId());

                    ApiFireBaseStore.addItem2DataBase(activity.mDatabaseRef ,item);

                }
            });

        }
    }




}
