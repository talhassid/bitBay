package com.bitbay.bitbay;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileInfoFragment extends Fragment {

    GoogleSignInAccount myAccount ;
    private TextView mIdTokenTextView;

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
        Log.e("temp-check data",name);
        // Inflate the layout for this fragment

        //views
        mIdTokenTextView = view.findViewById(R.id.detail);

        mIdTokenTextView.setText(getString(R.string.id_token_fmt, name));
//        mIdTokenTextView.setText(name);

        return view ;
    }

}
