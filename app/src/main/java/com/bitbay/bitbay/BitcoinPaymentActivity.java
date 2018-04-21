package com.bitbay.bitbay;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.google.android.gms.common.internal.zzbq.checkNotNull;


public class BitcoinPaymentActivity extends AppCompatActivity {

    //HARDCODED

    String MY_RECIPIENT_TEXTUAL_PUBLIC_KEY;
    final String LOG_TAG = "BitcoinActivity";

    TextView mMyBalanceTextView;
    TextView mProductPriceTextView;
    private ProgressBar spinner;
    String recipientPublicAddress;
    String price;

    long myBalance;
    BitcoinWalletService bitcoinService;
    ServiceConnection bitcoindServiceConnection;

    final static double SATOSHIS_IN_BITCOIN = 100000000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin_payment);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        recipientPublicAddress = (String) bundle.get("address");
        price = (String) bundle.get("price");

        mMyBalanceTextView = findViewById(R.id.tv_my_balance);
        mProductPriceTextView = findViewById(R.id.tv_product_price);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        loadWallet(); //async
    }

    public void onPay(View view)
    {
        if (bitcoinService == null) {
            Toast.makeText(getApplicationContext(), "Wallet is loading...", Toast.LENGTH_LONG);
            return;
        }
        long balance = bitcoinService.getBalance();
        if (balance == -1) {
            return; // there is a toast
        }
        long priceLong = Long.parseLong(price);
        double priceInBitcoin = 8810.5213 * priceLong;
        long priceInSatoshis = (long)(priceInBitcoin * SATOSHIS_IN_BITCOIN);

        if (balance < priceInSatoshis) {
            Toast.makeText(getApplicationContext(), "Not Enough In Wallet", Toast.LENGTH_LONG);
            return;
        }
        try {
            bitcoinService.pay(recipientPublicAddress, priceInSatoshis);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "There is tax on transaction :)", Toast.LENGTH_LONG);
        }
    }

    public void loadWallet()
    {
        Intent bindToWalletIntent = new Intent(getApplicationContext(), BitcoinWalletService.class);
        bitcoindServiceConnection= new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BitcoinWalletService.BitcoinWalletBinder binder = (BitcoinWalletService.BitcoinWalletBinder) service;
                bitcoinService = binder.getService();
                myBalance = bitcoinService.getBalance();
                mMyBalanceTextView.setText("Bitcoin Balance (Satoshis): " + myBalance/1000);
                Toast.makeText(getApplicationContext(), "Ready To Pay", Toast.LENGTH_LONG);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(bindToWalletIntent, bitcoindServiceConnection, 0);
    }

    @Override
    protected void onDestroy() {
        unbindService(bitcoindServiceConnection);
        super.onDestroy();
    }
}






















