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

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class historyListAdapter extends ArrayAdapter<StoreItem> {

    ArrayList<StoreItem> items;
    Context context;
    int resource ;
    DatabaseReference mDatabaseRef;


    public historyListAdapter(Context context, int resource, ArrayList<StoreItem> items,
                              DatabaseReference mDatabaseRef){
        super(context, resource,items);
        this.items = items;
        this.context = context;
        this.resource = resource ;
        this.mDatabaseRef = mDatabaseRef;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ImgHolder holder;
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.history_items_list_view,parent,false);

            holder = new ImgHolder();
            holder.IMAGE = convertView.findViewById(R.id.h_imageViewItem);
            holder.PRICE = convertView.findViewById(R.id.h_textViewPrice);
            holder.DESCRIPTION = convertView.findViewById(R.id.h_textViewDesription);

            convertView.setTag(holder);
        }else {
            holder = (ImgHolder)convertView.getTag();
        }

        final StoreItem item = getItem(position);

        Picasso.get().load(item.getImagePath()).into(holder.IMAGE);
        holder.PRICE.setText(item.getPrice());
        holder.DESCRIPTION.setText(item.getDescription());
        return convertView;

    }

    static class ImgHolder
    {
        ImageView IMAGE;
        TextView PRICE;
        TextView DESCRIPTION;
    }

}
