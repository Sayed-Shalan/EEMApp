package com.dasta.eemapp.adapter.Client;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
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
import com.dasta.eemapp.fragment.Client.Offer;
import com.dasta.eemapp.fragment.Client.OfferProfile;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.setwget;

import java.util.ArrayList;

public class Adapter_CustomList_All_Offer extends BaseAdapter implements View.OnClickListener {

    private Offer shop;
    private Activity activity;
    private static LayoutInflater inflater = null;
    public Resources res;
    int i = 0;
    setwget tempValues = null;
    ArrayList data;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public Adapter_CustomList_All_Offer(Activity a, ArrayList d, Resources resLocal, Offer shop1) {

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
        public TextView txtTitle;
        public TextView percentTxt;
        public TextView afterTxt;
        TextView title;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /********** Inflate tabitem.xml file for each row ( Defined below ) ************/
            vi = inflater.inflate(R.layout.row_list_all_offer_profile, parent, false);

            vi.requestLayout();

            /******** View Holder Object to contain tabitem.xml file elements ************/
            holder = new ViewHolder();

            holder.txtTitle = (TextView) vi.findViewById(R.id.textViewOfferProfile);
            holder.networkImageView = (ImageView) vi.findViewById(R.id.photoViewOfferProfile);
            holder.percentTxt=(TextView) vi.findViewById(R.id.textViewOfferProfileSalePercent);
            holder.afterTxt=(TextView) vi.findViewById(R.id.textViewOfferProfileAfterSale);
            holder.title=(TextView) vi.findViewById(R.id.textViewOfferProfiletitle);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        if (data.size() <= 0) {
            holder.txtTitle.setText("No Data");
            holder.networkImageView.setImageResource(R.drawable.logo_home);

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (setwget) data.get(position);

            /************  Set Model values in Holder elements ***********/

        holder.title.setText(tempValues.getTitle());
            holder.txtTitle.setText( tempValues.getPrice() +
                    "  EGP");
            holder.percentTxt.setText(tempValues.getPercentage().concat(" %"));

            double afterSale=Double.valueOf(tempValues.getPrice())-(Double.valueOf(tempValues.getPrice())*
                    (Double.valueOf(tempValues.getPercentage())/100));

             String salePrceafter=String.format("%.2f", afterSale);

            holder.afterTxt.setText(salePrceafter.concat(" EGP"));

            //holder.networkImageView.setImageUrl(tempValues.getImgurl(), imageLoader);
            Glide.with(activity).load(tempValues.getImgurl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.networkImageView);


            if (holder.networkImageView.getDrawable() == null) {
                holder.networkImageView.setImageResource(R.drawable.logo_home);
            } else {
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
                Toast.makeText(activity, "حدث خطأ ما...", Toast.LENGTH_LONG).show();
            } else {
                shop.onItemClick(mPosition);
            }
        }
    }

}