package com.dasta.eemapp.adapter.Client;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Home_Shop;
import com.dasta.eemapp.fragment.Client.Shop;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.setwget;

import java.util.ArrayList;


/**
 * Created by Mohamed on 06/11/2015.
 */
public class Adapter_CustomList_All_Shop extends BaseAdapter implements View.OnClickListener {

    private Shop shop;
    private Activity activity;
    private static LayoutInflater inflater = null;
    public Resources res;
    int i = 0;
    setwget tempValues = null;
    ArrayList data;


    public Adapter_CustomList_All_Shop(Activity a, ArrayList d, Resources resLocal, Shop shop1) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;
        shop = shop1;


        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*********
     * Create a holder to contain inflated xml file elements
     ***********/
    public static class ViewHolder {

        public ImageView networkImageView;
        public TextView txtTitle, txtAddress, txtPhone;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /********** Inflate tabitem.xml file for each row ( Defined below ) ************/
            vi = inflater.inflate(R.layout.row_list_all_shop, parent, false);

            vi.requestLayout();

            /******** View Holder Object to contain tabitem.xml file elements ************/
            holder = new ViewHolder();

            holder.txtTitle = (TextView) vi.findViewById(R.id.txtShopTitle);
            holder.txtAddress = (TextView) vi.findViewById(R.id.txtShopAddress);
            holder.txtPhone = (TextView) vi.findViewById(R.id.txtShopPhone);
            holder.networkImageView = (ImageView) vi.findViewById(R.id.nShopImage);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();


        if (data.size() <= 0) {
            holder.txtTitle.setText("No Data");
            holder.networkImageView.setImageResource(R.drawable.logo_home);

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (setwget) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtTitle.setText(tempValues.getTitle());
            holder.txtAddress.setText(tempValues.getAddress());
            holder.txtPhone.setText(tempValues.getPhone());
            // holder.networkImageView.setImageResource(tempValues.getImgurl(), imageLoader);

            Glide.with(activity).load(tempValues.getImgurl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.networkImageView);


        }
        vi.setOnClickListener(new OnItemClickListener(position));

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            if (data.size() <= 0) {
                Toast.makeText(activity, "No Data", Toast.LENGTH_LONG).show();
            } else {
                shop.onItemClick(mPosition);
            }
        }
    }

}