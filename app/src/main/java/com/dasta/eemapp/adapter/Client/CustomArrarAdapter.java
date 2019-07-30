package com.dasta.eemapp.adapter.Client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dasta.eemapp.R;

import java.util.List;


public class CustomArrarAdapter extends ArrayAdapter<String> {
    public CustomArrarAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CustomArrarAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =convertView;
        if(v==null)
        {
            LayoutInflater vi;
            vi=LayoutInflater.from(getContext());
            v=vi.inflate(R.layout.custom_array_adapter_two_text_views,null);
        }
        String p = getItem(position);
        if (p!=null)
        {
            TextView textView=(TextView)v.findViewById(R.id.textView);
            textView.setText(p);
            if (position==0||position==1){
                //textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_unlocked_lock,0);
                textView.setTextColor(getContext().getResources().getColor(R.color.black));
            }

        }
        return v;
    }
}
