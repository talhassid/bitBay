package com.bitbay.bitbay;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BitcoinCartActivity extends AppCompatActivity {

    private HashMap<String,String> cartItems;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        cartItems = (HashMap<String, String>) bundle.get("cart items");
        payOnItem();

    }

    public void payOnItem(){
        Iterator it = cartItems.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String address = (String) pair.getKey();
            String price = (String) pair.getValue();
            it.remove();
            Intent bitCoinIntent = new Intent(BitcoinCartActivity.this, BitcoinPaymentActivity.class);
            bitCoinIntent.putExtra("price", price);
            bitCoinIntent.putExtra("address", address);
            startActivity(bitCoinIntent);
        }
        finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        payOnItem();
    }
}
