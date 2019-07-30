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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Shop.Product;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.setwget;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Mohamed on 06/11/2015.
 */
public class Adapter_CustomList_All_Product extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private Product product;
    private static LayoutInflater inflater = null;
    public Resources res;
    public static int i = 0;
    setwget tempValues = null;
    ArrayList data;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public Adapter_CustomList_All_Product(Activity a, ArrayList d, Resources resLocal, Product ro) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;
        product = ro;


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

        //public NetworkImageView networkImageView;
        public ImageView imageView;
        public TextView txtTitle, txtDept, txtDesc, txtDeliver, txtReservation, txtBuy, txtSell;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /********** Inflate tabitem.xml file for each row ( Defined below ) ************/
            vi = inflater.inflate(R.layout.row_list_all_product, parent, false);

            vi.requestLayout();

            /******** View Holder Object to contain tabitem.xml file elements ************/
            holder = new ViewHolder();

            holder.txtTitle = (TextView) vi.findViewById(R.id.txtProductTitle);
            holder.txtBuy = (TextView) vi.findViewById(R.id.txtProductBuyPrice);
            holder.txtSell = (TextView) vi.findViewById(R.id.txtProductSellPrice);
            holder.txtDesc = (TextView) vi.findViewById(R.id.txtProductDesc);
            holder.txtDept = (TextView) vi.findViewById(R.id.txtProductDepartment);
            holder.txtDeliver = (TextView) vi.findViewById(R.id.txtProductDeliver);
            holder.txtReservation = (TextView) vi.findViewById(R.id.txtProductReservation);
            //holder.networkImageView = (NetworkImageView) vi.findViewById(R.id.nProductImage);

            holder.imageView = (ImageView) vi.findViewById(R.id.nProductImage);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        if (data.size() <= 0) {
            holder.txtTitle.setText(R.string.noData);
//            holder.networkImageView.setDefaultImageResId(R.drawable.logo_home);

            holder.imageView.setImageResource(R.drawable.logo_home);
        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (setwget) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtTitle.setText(tempValues.getTitle());
            holder.txtReservation.setText(tempValues.getReservation());
            holder.txtSell.setText("سعر البيع :" + tempValues.getSell());
            holder.txtBuy.setText("سعر الشراء :" + tempValues.getBuy());
            holder.txtDept.setText(tempValues.getDept());
            holder.txtDesc.setText(tempValues.getDesc());
            holder.txtDeliver.setText(tempValues.getDeliver());
            //AppController.getInstance().getRequestQueue().getCache().remove(tempValues.getImgurl());
            //holder.networkImageView.setImageUrl(tempValues.getImgurl(), imageLoader);

            /*Picasso.with(activity).setLoggingEnabled(true);
            Picasso.with(activity).load(tempValues.getImgurl())
                    .error(R.drawable.logo_home)
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.imageView);*/
            /*byte[] pImage = tempValues.getImgLocal();
            Bitmap bitmap = BitmapFactory.decodeByteArray(pImage, 0, pImage.length);
            holder.imageView.setImageBitmap(bitmap);*/


            Glide.with(activity).load(tempValues.getImgurl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.imageView);

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
                product.onItemClick(mPosition);
            }
        }
    }

}