package com.bitbay.bitbay;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {

    private Button uploadButton;
    private Button categoryButton;
    private ProfileActivity activity;
    private EditText mPrice;
    private EditText mDescription;

    String[] listCategories;
    boolean[] checkedBox;
    ArrayList<Integer> mItemCategories = new ArrayList<>();
    ArrayList<String> myCategories = new ArrayList<>();

    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        activity = (ProfileActivity) getActivity();
        uploadButton = view.findViewById(R.id.upload_button);
        categoryButton = view.findViewById(R.id.category_button);

        listCategories = getResources().getStringArray(R.array.category_list);
        checkedBox = new boolean[listCategories.length];

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity); // verify that activity is good and not context/
                mBuilder.setTitle("chose category for you product");
                mBuilder.setMultiChoiceItems(listCategories, checkedBox, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            if(!mItemCategories.contains(position)){
                                mItemCategories.add(position);
                            }else {
                                mItemCategories.remove(position);
                            }
                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String category = "";
                        for(int i=0 ; i <mItemCategories.size();i++){
                            category = category + listCategories[mItemCategories.get(i)] + ",";

                        }
                        myCategories.add(category);
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        myCategories.clear();
                        for (int i =0 ; i < checkedBox.length ; i ++){
                            checkedBox[i] = false;
                            mItemCategories.clear();
                            myCategories.clear();

                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

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
                            activity.myAccount.getId(), myCategories.toString());

                    myCategories.clear(); // clear the categories from old upload to new
                    ApiFireBaseStore.addItem2DataBase(activity.mDatabaseRef ,item);

                }
            });

        }
    }




}
