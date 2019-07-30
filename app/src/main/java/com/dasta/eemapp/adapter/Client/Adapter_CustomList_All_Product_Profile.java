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
import com.dasta.eemapp.fragment.Client.ProductProfile;
import com.dasta.eemapp.fragment.Client.Shop;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.setwget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Mohamed on 06/11/2015.
 */
public class Adapter_CustomList_All_Product_Profile extends BaseAdapter implements View.OnClickListener {

    private ProductProfile shop;
    private Activity activity;
    private static LayoutInflater inflater = null;
    public Resources res;
    int i = 0;
    setwget tempValues = null;
    ArrayList data;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public Adapter_CustomList_All_Product_Profile(Activity a, ArrayList d, Resources resLocal, ProductProfile shop1) {

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

    public static class ViewHolder {

        public ImageView networkImageView;
        public TextView txtTitle;
        public TextView priceTxt;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /********** Inflate tabitem.xml file for each row ( Defined below ) ************/
            vi = inflater.inflate(R.layout.row_list_all_product_profile, parent, false);

            vi.requestLayout();

            /******** View Holder Object to contain tabitem.xml file elements ************/
            holder = new ViewHolder();

            holder.txtTitle = (TextView) vi.findViewById(R.id.productProfileTitle);
            holder.networkImageView = (ImageView) vi.findViewById(R.id.productProfileImg);
            holder.priceTxt=(TextView) vi.findViewById(R.id.productProfilePrice);


            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        if (data.size() <= 0) {
            holder.txtTitle.setText("غير معرف");
            holder.networkImageView.setImageResource(R.drawable.logo_home);

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (setwget) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtTitle.setText(tempValues.getTitle());
            holder.priceTxt.setText(tempValues.getPrice().concat(" EGP"));
            // holder.networkImageView.setImageResource(tempValues.getImgurl(), imageLoader);
            Glide.with(activity).load(tempValues.getImgurl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.networkImageView);

            if (holder.networkImageView.getDrawable() == null) {
                holder.networkImageView.setImageResource(R.drawable.logo_home);
            } else {
                //holder.networkImageView.setImageResource(tempValues.getImgurl(), imageLoader);
                Glide.with(activity).load(tempValues.getImgurl())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.networkImageView);
            }

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