package com.bitbay.bitbay;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roeis on 4/6/2018.
 */

public class CustomListAdapter extends ArrayAdapter<StoreItem> {

    ArrayList<StoreItem> items;
    Context context;
    int resource ;


    public CustomListAdapter(Context context, int resource, ArrayList<StoreItem> items) {
        super(context, resource,items);
        this.items = items;
        this.context = context;
        this.resource = resource ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ImgHolder holder;
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_items_list_view,parent,false);

            holder = new ImgHolder();
            holder.IMAGE = convertView.findViewById(R.id.imageViewItem);
            holder.PRICE = convertView.findViewById(R.id.textViewPrice);
            holder.DESCRIPTION = convertView.findViewById(R.id.textViewDesription);

            convertView.setTag(holder);
        }else {
            holder = (ImgHolder)convertView.getTag();
        }

        StoreItem item = getItem(position);

        Picasso.get().load(item.getImagePath()).into(holder.IMAGE);
        holder.PRICE.setText(item.getPrice());
        holder.DESCRIPTION.setText(item.getDescription());

        return convertView;


//        ImageView imageView = convertView.findViewById(R.id.imageViewItem);
//        Picasso.get().load(item.getImagePath()).into(imageView);
//
//        TextView txtPrice = convertView.findViewById(R.id.textViewPrice);
//        txtPrice.setText(item.getPrice());
//
//        TextView txtDescription = convertView.findViewById(R.id.textViewDesription);
//        txtDescription.setText(item.getDescription());

//        return super.getView(position, convertView, parent);
    }

    static class ImgHolder
    {
        ImageView IMAGE;
        TextView PRICE;
        TextView DESCRIPTION;
    }

}
