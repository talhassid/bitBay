package com.bitbay.bitbay;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by roeis on 4/6/2018.
 */

public class CategoriesListAdapter extends ArrayAdapter<StoreItem> {

    ArrayList<StoreItem> items;
    Context context;
    int resource ;
    GoogleSignInAccount myAccount = null ;
    DatabaseReference mDatabaseRef;


    public CategoriesListAdapter(Context context, int resource, ArrayList<StoreItem> items,
                                 GoogleSignInAccount myAccount,DatabaseReference mDatabaseRef) {
        super(context, resource,items);
        this.items = items;
        this.context = context;
        this.resource = resource ;
        this.myAccount = myAccount;
        this.mDatabaseRef = mDatabaseRef;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ImgHolder holder;
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.category_items_list_view,parent,false);

            holder = new ImgHolder();
            holder.IMAGE = convertView.findViewById(R.id.imageViewItem);
            holder.PRICE = convertView.findViewById(R.id.textViewPrice);
            holder.DESCRIPTION = convertView.findViewById(R.id.textViewDesription);
            holder.Button = convertView.findViewById(R.id.add_to_cart_button);

            convertView.setTag(holder);
        }else {
            holder = (ImgHolder)convertView.getTag();
        }

        final StoreItem item = getItem(position);

        Picasso.get().load(item.getImagePath()).into(holder.IMAGE);
        holder.PRICE.setText(item.getPrice());
        holder.DESCRIPTION.setText(item.getDescription());
        holder.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("info: ","ADD TO CART BUTTON PRESSED");
                ApiFireBaseStore.addItem2Cart(mDatabaseRef,item,myAccount);
            }
        });


        return convertView;

    }

    static class ImgHolder
    {
        ImageView IMAGE;
        TextView PRICE;
        TextView DESCRIPTION;
        Button Button;
    }

}
