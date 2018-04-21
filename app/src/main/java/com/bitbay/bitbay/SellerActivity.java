package com.bitbay.bitbay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SellerActivity extends AppCompatActivity {
    private TextView sellerData;
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<HashMap<String, String>>> listHash;
    int MY_REQUEST_ID = 333;
    String sellerKey;
    double currentRate = 0;
    int currentNumVoters = 0;
    String ntificationToken;
    final FirebaseMessagingClient fcmClient = new FirebaseMessagingClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        listView = (ExpandableListView) findViewById(R.id.sellerItems);


        //Getting data from prev activity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        sellerKey = (String) bundle.get("sellerKey");



        final DatabaseReference seller
                = FirebaseDatabase.getInstance().getReference().child("users").child(sellerKey);
        seller.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("name").getValue());
                String email = String.valueOf(dataSnapshot.child("email").getValue());
                String rateValue = String.valueOf(dataSnapshot.child("rate").child("value")
                        .getValue());
                String rateVoters = String.valueOf(dataSnapshot.child("rate").child("numVoters")
                        .getValue());
                ntificationToken = String.valueOf(dataSnapshot.child("notificationToken")
                        .getValue());
                fcmClient.sendMessage(ntificationToken, "bitBay", "Someone is watching your " +
                        "profile.", "Not sure if this field is mandatory");
                Log.i("seller Token ", ntificationToken);
                if (rateValue.equals("null")) {
                    currentRate = 0;
                } else {
                    currentRate = Double.parseDouble(rateValue);
                }
                if (rateVoters.equals("null")) {
                    currentNumVoters = 0;
                } else {
                    currentNumVoters = Integer.parseInt(rateVoters);
                }
                sellerData = findViewById(R.id.sellerData);
                sellerData.setText("\nName: " + name + "\n\nEmail: " + email);
                TextView star = (TextView) findViewById(R.id.star);
                star.setText(String.valueOf(currentRate));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final DatabaseReference itemsRef
                = FirebaseDatabase.getInstance().getReference().child("items");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap items = (HashMap) dataSnapshot.getValue();

                List<HashMap<String, String>> detailedItems = new ArrayList<HashMap<String,
                        String>>();


                Iterator it = items.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    String itemKey = (String) pair.getKey();
                    HashMap<String, String> itemValue = (HashMap<String, String>) pair.getValue();
                    String userId = itemValue.get("userId");
                    if (userId.equals(sellerKey) == false) {
                        continue;
                    }
                    HashMap<String, String> item = new HashMap<>();
                    item.put("description", itemValue.get("description"));
                    item.put("price", itemValue.get("price"));
                    item.put("imagePath", itemValue.get("storagePath"));

                    detailedItems.add(item);

                }
                initData(detailedItems);
                listAdapter = new ExpandableListAdapter(SellerActivity.this, listDataHeader,
                        listHash);
                listView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Button rateButton = (Button) findViewById(R.id.rate_button);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rateIntent = new Intent(SellerActivity.this, RateActivity.class);
                rateIntent.putExtra("currentRate", currentRate);
                rateIntent.putExtra("numVoted", currentNumVoters);
                //startActivity(rateIntent);
                startActivityForResult(rateIntent, MY_REQUEST_ID);
            }
        });
    }

    private void initData(List detailedItems) {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Seller Items");

        listHash.put(listDataHeader.get(0), detailedItems);

    }


    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == MY_REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                double newRate = (double) bundle.get("newRate");
                int numVoters = (int) bundle.get("numVoters");
                final DatabaseReference seller
                        = FirebaseDatabase.getInstance().getReference().child("users").child
                        (sellerKey);
                seller.child("rate").child("value").setValue(String.valueOf(newRate));
                seller.child("rate").child("numVoters").setValue(String.valueOf(numVoters));
                fcmClient.sendMessage(ntificationToken, "bitBay", "Your rate was changed to " + String.valueOf(newRate),
                        "Not sure if this field is mandatory");
                Log.i("seller Token ", ntificationToken);

            }
        }
    }
}
