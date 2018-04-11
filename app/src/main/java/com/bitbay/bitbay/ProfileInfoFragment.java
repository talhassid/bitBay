package com.bitbay.bitbay;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileInfoFragment extends Fragment {

    GoogleSignInAccount myAccount ;
    private TextView mIdTokenTextView;
    private ImageView iv;

    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);
        ProfileActivity activity = (ProfileActivity) getActivity();
        myAccount = activity.getMyAccount();
        String name = myAccount.getDisplayName();
        String email = myAccount.getEmail();
        String id = myAccount.getId();
        Uri personPhoto = myAccount.getPhotoUrl();


        //views
        mIdTokenTextView = view.findViewById(R.id.detail);
        mIdTokenTextView.setText("______________________________"+"\nName: \n"+ name
                +"\n______________________________"+"\nEmail: \n"+email +
                "\n______________________________"+"\nUser number: \n"+id
                +"\n______________________________");

        iv = view.findViewById(R.id.imageView1);
        Picasso.get().load(personPhoto).resize(250,350).centerCrop().into(iv);
//        iv.setImageURI(personPhoto);

        return view ;
    }
}
