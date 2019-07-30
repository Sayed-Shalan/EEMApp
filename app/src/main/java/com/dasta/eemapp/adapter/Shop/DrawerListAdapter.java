package com.dasta.eemapp.adapter.Shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.Shop.NavItem;

import java.util.ArrayList;

/**
 * Created by Mohamed on 24/10/2015.
 */
public class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;
    Context context1;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        context1 = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_drawer, null);
        } else {
            view = convertView;
        }

        TextView iconView = (TextView) view.findViewById(R.id.icon);
        iconView.setText(mNavItems.get(position).mIcon);


        return view;
    }
}
