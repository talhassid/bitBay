package com.bitbay.bitbay;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;


/**
 * A simple {@link Fragment} subclass.
 */
public class myOffersFragment extends Fragment {

    private ListView mListItems ;
    private ProfileActivity activity;
    private final long[] val = new long[1];
//    private long itemsCount;

    public myOffersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (ProfileActivity) getActivity();

        View rootView = inflater.inflate(R.layout.fragment_my_offers, container, false);
        ListView listView= rootView.findViewById(R.id.myItems);
        OnGetDataListener listener = createListner(val);
        String[] items = {String.valueOf(val[0]),"item2","item3"};
        CustomAdapter customAdapter = new CustomAdapter (activity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1, items);

        ApiFireBaseStore.readDataOnce(activity.mDatabaseRef,activity.myAccount,val,listener);

        listView.setAdapter(adapter);



        return rootView;
    }

    class CustomAdapter extends BaseAdapter{

        Context context;

        public CustomAdapter(Context con) {
            context = con;
            Resources res = con.getResources();
        }

        @Override
        public int getCount() {
            return (int)val[0];
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate(R.layout.custom_items_list_view,null);
            TextView price = view.findViewById(R.id.textView3);
            price.setText((int)val[0]);
            Log.e("*getView", String.valueOf(val[0]));
            return view;
        }
    }


    public OnGetDataListener createListner( final long[] val) {
        OnGetDataListener listener = new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                val[0] = dataSnapshot.getChildrenCount();
                Log.e("inside on success", String.valueOf(val[0]));
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        };
    return listener;
    }
}