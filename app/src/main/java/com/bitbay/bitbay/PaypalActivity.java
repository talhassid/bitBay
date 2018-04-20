package com.bitbay.bitbay;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaypalActivity extends AppCompatActivity {

    TextView m_response;
    PayPalConfiguration m_configuration;
    //the id is the link to the paypal account
    String m_paypalClientId =
            "ATom87_nHy9ovxAQo1Yv4q8nwfHHVwwT8w13LwX8eDf22v0BGrYYsoRdw5n9S7BTtvEVFsHHHewh6Jcu";
    Intent m_service;
    int m_paypalRequestCode = 999; //
    int price;
    TextView totalCartValue;
    GoogleSignInAccount myAccount;
    HashMap<String,String>  cartItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        price = (int) bundle.get("price");
        cartItems = (HashMap<String,String>) bundle.get("cart items");

        myAccount = (GoogleSignInAccount) bundle.get("gAccount");
        String s_price = String.valueOf(price);
        totalCartValue = findViewById(R.id.total_amount);
        totalCartValue.setText("Total cart value is " + s_price + " $");
        m_response = (TextView) findViewById(R.id.response);
        m_configuration = new PayPalConfiguration().environment(PayPalConfiguration
                .ENVIRONMENT_SANDBOX)
                .clientId(m_paypalClientId);
        m_service = new Intent(this, PayPalService.class);
        m_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
        startService(m_service); //paypal service, listening to calls to paypal app

        Button bitcoinPayment = (Button) findViewById(R.id.bitcoinPayment);
        Button bitcoinValue = (Button) findViewById(R.id.bitcoinValue);
    }

    void pay(View view) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(price), "USD",
                "Test payment with paypal", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, m_paypalRequestCode);
    }

    void moveToBitCoinPayment(View view) {
        Intent bitCoinIntent = new Intent(PaypalActivity.this, BitcoinCartActivity.class);
        bitCoinIntent.putExtra("price", price);
        bitCoinIntent.putExtra("cart items", cartItems);
        startActivity(bitCoinIntent);
    }

    void moveToBitCoinValue(View view) {
        Intent bitCoinIntent = new Intent(PaypalActivity.this, BitcoinValueActivity.class);
        bitCoinIntent.putExtra("price", price);
        startActivity(bitCoinIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == m_paypalRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity
                        .EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    String state = confirmation.getProofOfPayment().getState();
                    if (state.equals("approved")) {
                        m_response.setText("payment approved");
                        Toast.makeText(getApplication(), "Payment Approved", Toast.LENGTH_SHORT)
                                .show();

                        Intent cartIntent = new Intent(this, ProfileActivity.class);
                        cartIntent.putExtra("target", "cart");
                        cartIntent.putExtra("account", myAccount);
                        cartIntent.putExtra("callingSrc", "paypal");
                        startActivity(cartIntent);
                    } else
                        m_response.setText("error in the payment");
                } else
                    m_response.setText("Confirmation is null");
            }
        }
    }
}
