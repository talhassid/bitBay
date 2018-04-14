package com.bitbay.bitbay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<HashMap<String, String>>> listHashMap;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<HashMap<String, String>>> listHashMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {

        if (listDataHeader != null) {
            return listDataHeader.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        if (listDataHeader != null && listHashMap != null) {
            return listHashMap.get(listDataHeader.get(i)).size();
        }
        return 0;

    }

    @Override
    public Object getGroup(int i) {
        if (listDataHeader != null) {
            return listDataHeader.get(i);
        }
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        if (listDataHeader != null && listHashMap != null) {
            return listHashMap.get(listDataHeader.get(i)).get(i1); // i = GroupItem , i1=ChildItem
        }
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String) getGroup(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_group, null);
        }
        TextView lblListHeader = (TextView) view.findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        final HashMap<String, String> childData = (HashMap<String, String>) getChild(i, i1);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }


        TextView txtListChild1 = (TextView) view.findViewById(R.id.itemDescription);
        TextView txtListChild2 = (TextView) view.findViewById(R.id.itemPrice);
        ImageView imageListChild = (ImageView) view.findViewById(R.id.imageViewItem);
        txtListChild1.setText(childData.get("description"));
        txtListChild2.setText(childData.get("price")+" $");
        Picasso.get().load(childData.get("imagePath")).into(imageListChild);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
