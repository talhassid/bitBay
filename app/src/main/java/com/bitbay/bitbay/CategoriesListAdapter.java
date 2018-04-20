package com.bitbay.bitbay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.net.MediaType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CategoriesListAdapter extends ArrayAdapter<StoreItem> {

    ArrayList<StoreItem> items;
    Context context;
    int resource ;
    GoogleSignInAccount myAccount = null ;
    DatabaseReference mDatabaseRef;
    ImgHolder holder;

    public CategoriesListAdapter(Context context, int resource, ArrayList<StoreItem> items,
                                 GoogleSignInAccount myAccount,DatabaseReference mDatabaseRef) {
        super(context, resource,items);
        this.items = items;
        this.context = context;
        this.resource = resource ;
        this.myAccount = myAccount;
        this.mDatabaseRef = mDatabaseRef;
    }

    public void updateHolderSellerName(StoreItem item, String sellerName){
        holder.SELLER_NAME.setText(sellerName);
        final String sellerKey = item.getSellerKey();
        holder.SELLER_NAME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sellerPage = new Intent(getContext(), SellerActivity.class);
                sellerPage.putExtra("sellerKey", sellerKey);
                context.startActivity(sellerPage);
            }
        });

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.category_items_list_view,parent,false);

            holder = new ImgHolder();
            holder.IMAGE = convertView.findViewById(R.id.imageViewItem);
            holder.PRICE = convertView.findViewById(R.id.textViewPrice);
            holder.DESCRIPTION = convertView.findViewById(R.id.textViewDesription);
            holder.SELLER_NAME = convertView.findViewById(R.id.sellerName);
            holder.Button = convertView.findViewById(R.id.add_to_cart_button);

            convertView.setTag(holder);
        }else {
            holder = (ImgHolder)convertView.getTag();
        }

        final StoreItem item = getItem(position);

        Picasso.get().load(item.getImagePath()).into(holder.IMAGE);
        holder.PRICE.setText(item.getPrice());
        holder.DESCRIPTION.setText(item.getDescription());
        final DatabaseReference seller
                = this.mDatabaseRef.child("users").child(item.getSellerKey()).child("name");
        seller.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sellerName = dataSnapshot.getValue(String.class);
                updateHolderSellerName(item, sellerName);
                if (sellerName == null){
                    Log.i("seller name is null ",":(");
                }
                else{
                    Log.i("seller name: ",sellerName);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        holder.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("info: ","ADD TO CART BUTTON PRESSED");
                ApiFireBaseStore.addItem2Cart(mDatabaseRef, item, myAccount.getId());

                String sellerToken = "cKRI28GDBxs:APA91bETAOZoZ9pFFuYToBz2nCiD5ryTlC_PTy7ARl641IuXQgZFKV9fPLC2ruf45Q5v4sDzWuZAkWzkdYRP9KI3HZsZPvGZyjmka7cE8AzNz7xgAmSryPU_Ee-jvmYw22pYfqZ7p_h2";

                FirebaseMessagingClient fcmClient = new FirebaseMessagingClient();
                fcmClient.sendMessage(sellerToken, "walla", "Kuala bear", null, "da fuck");

                Toast.makeText(getContext(), "Added to cart successfully", Toast.LENGTH_SHORT).show();

            }
        });


        return convertView;

    }

    static class ImgHolder
    {
        ImageView IMAGE;
        TextView PRICE;
        TextView DESCRIPTION;
        Button SELLER_NAME;
        Button Button;
    }

}
