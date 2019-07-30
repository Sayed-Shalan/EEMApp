package com.dasta.eemapp.adapter.Shop;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Shop.Offer;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.setwget;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Mohamed on 06/11/2015.
 */
public class Adapter_CustomList_All_Offer extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private Offer offer;
    private static LayoutInflater inflater = null;
    public Resources res;
    int i = 0;
    setwget tempValues = null;
    ArrayList data;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public Adapter_CustomList_All_Offer(Activity a, ArrayList d, Resources resLocal, Offer activityOffer) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;
        offer = activityOffer;

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
        public TextView txtTitle, txtBuy, txtSell, txtPercentage;
        public CheckBox chSale;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /********** Inflate tabitem.xml file for each row ( Defined below ) ************/
            vi = inflater.inflate(R.layout.row_list_all_offer, parent, false);

            vi.requestLayout();

            /******** View Holder Object to contain tabitem.xml file elements ************/
            holder = new ViewHolder();

            holder.txtTitle = (TextView) vi.findViewById(R.id.txtProductOfferTitle);
            holder.txtBuy = (TextView) vi.findViewById(R.id.txtBuyOfferPrice);
            holder.txtSell = (TextView) vi.findViewById(R.id.txtSellOfferPrice);
            holder.txtPercentage = (TextView) vi.findViewById(R.id.txtOfferPercentage);
            holder.chSale = (CheckBox) vi.findViewById(R.id.chSale);
            holder.networkImageView = (ImageView) vi.findViewById(R.id.nProductOfferImage);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        /*if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();*/

        if (data.size() <= 0) {
            holder.txtTitle.setText(R.string.noData);
            holder.networkImageView.setImageResource(R.drawable.logo_home);
            holder.chSale.setEnabled(false);

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (setwget) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtTitle.setText(tempValues.getTitle());
            holder.txtSell.setText(tempValues.getSell());
            holder.txtBuy.setText(tempValues.getBuy());
            holder.txtPercentage.setText(tempValues.getDesc());
            holder.chSale.setEnabled(false);
            if (tempValues.getDept().equals("1")) {
                holder.chSale.setChecked(true);
            } else {
                holder.chSale.setChecked(false);
            }
            //holder.networkImageView.setImageUrl(tempValues.getImgurl(), imageLoader);

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
                Toast.makeText(activity, R.string.noData, Toast.LENGTH_LONG).show();
            } else {
                offer.onItemClick(mPosition);
            }
        }
    }

}