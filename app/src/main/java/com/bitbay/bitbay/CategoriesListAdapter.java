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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import com.google.firebase.messaging.FirebaseMessaging.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by roeis on 4/6/2018.
 */

public class CategoriesListAdapter extends ArrayAdapter<StoreItem> {

    ArrayList<StoreItem> items;
    Context context;
    int resource ;
    GoogleSignInAccount myAccount = null ;
    DatabaseReference mDatabaseRef;
    ImgHolder holder;


    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final okhttp3.MediaType JSON
            = okhttp3.MediaType.parse("application/json; charset=utf-8");
    OkHttpClient mClient = new OkHttpClient();

    @SuppressLint("StaticFieldLeak")
    public void sendMessage(final JSONArray rec, final String title, final String body, final String icon, final String message) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {

                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", rec);
                    //root.put("to", destToken);

                    String result = postToFCM(root.toString());
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {
        String legacy_key = "AIzaSyD8mX0WRhMdQlI7weDeZrxBVe4p5c9bJz4";
        RequestBody body = RequestBody.create(JSON, bodyString);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("Authorization", "key=" +legacy_key)
                .url(FCM_MESSAGE_URL)
                .post(body)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }


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
                ApiFireBaseStore.addItem2Cart(mDatabaseRef,item,myAccount.getId());
                String sellerToken = "cKRI28GDBxs:APA91bETAOZoZ9pFFuYToBz2nCiD5ryTlC_PTy7ARl641IuXQgZFKV9fPLC2ruf45Q5v4sDzWuZAkWzkdYRP9KI3HZsZPvGZyjmka7cE8AzNz7xgAmSryPU_Ee-jvmYw22pYfqZ7p_h2";

                JSONArray regArray = new JSONArray();
                regArray.put(sellerToken);
                regArray.put(item.getSellerKey());

                sendMessage(regArray, "walla", "Kuala bear", null, "da fuck");

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
