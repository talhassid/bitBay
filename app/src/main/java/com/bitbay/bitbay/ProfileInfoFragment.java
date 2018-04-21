package com.bitbay.bitbay;


import android.app.AlertDialog;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;

import org.bitcoinj.core.Address;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileInfoFragment extends Fragment {

    GoogleSignInAccount myAccount ;
    private TextView mIdTokenTextView;
    private TextView mBitcoinBalanceTextView;
    private AlertDialog addressDialog;
    private ImageView iv;

    private long mBitcoinBalance;
    private Address myWalletPublicAddress;
    private ServiceConnection bitcoinServiceConnection;

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

        mBitcoinBalanceTextView = view.findViewById(R.id.bitcoinBalance);
        mBitcoinBalanceTextView.setText("Loading bitcoin balance...");

        Button chargeWalletButton = view.findViewById(R.id.charge_wallet);
        chargeWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeWallet();
            }
        });

        getWalletData(); //async

        return view ;
    }

    private void getWalletData()
    {
        Intent bitcoinWalletServiceBindIntent =
                new Intent(getActivity().getApplicationContext(), BitcoinWalletService.class);
        bitcoinServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("", "Connected to bitcoin service. Retrieving data.");
                BitcoinWalletService.BitcoinWalletBinder binder =
                        (BitcoinWalletService.BitcoinWalletBinder) service;
                BitcoinWalletService bitcoinService = binder.getService();
                mBitcoinBalance = bitcoinService.getBalance();
                if (mBitcoinBalance == -1) {
                    return;
                }
                myWalletPublicAddress = bitcoinService.getCurrentAddress();

                mBitcoinBalanceTextView.setText("Bitcoin balance: " + mBitcoinBalance);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Log.i("", "Connecting to bitcoin service");
        getActivity().bindService(bitcoinWalletServiceBindIntent, bitcoinServiceConnection, 0);
    }

    public void chargeWallet(){
        Log.i("", "chargeWallet");
        if (myWalletPublicAddress == null) {
            Toast.makeText(getActivity().getApplication().getApplicationContext(),
                    "Wallet not loaded yet. Try later.", Toast.LENGTH_LONG).show();
            return;
        }

        String addressExplain = "You can send coins here: \n" + myWalletPublicAddress;
        addressDialog = new AlertDialog.Builder(getActivity()).create();
        addressDialog.setTitle("My Wallet Address");
        addressDialog.setMessage(addressExplain);
        addressDialog.show();
        Log.i("", "Wallet charge address: " + myWalletPublicAddress);
    }

    @Override
    public void onDestroy() {
        if (bitcoinServiceConnection != null) {
            getActivity().unbindService(bitcoinServiceConnection);
        }

        super.onDestroy();
    }
}
