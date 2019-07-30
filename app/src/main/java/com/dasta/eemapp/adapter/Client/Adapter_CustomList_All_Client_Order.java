package com.dasta.eemapp.adapter.Client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Client.ClientOrder;
import com.dasta.eemapp.fragment.Client.ProductProfile;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.setwget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Mohamed on 06/11/2015.
 */
public class Adapter_CustomList_All_Client_Order extends BaseAdapter implements View.OnClickListener {

    private ClientOrder shop;
    private Activity activity;
    private static LayoutInflater inflater = null;
    public Resources res;
    int i = 0;
    setwget tempValues = null;
    ArrayList data;

    public Adapter_CustomList_All_Client_Order(Activity a, ArrayList d, Resources resLocal, ClientOrder shop1) {

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

        public CircleImageView imgOrderProduct, imgOrderShop, imgOrderPhone;
        public TextView txtOrderPname, txtOrderSAddress, txtOrderSName, txtOrderState, txtOrderPCode;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /********** Inflate tabitem.xml file for each row ( Defined below ) ************/
            vi = inflater.inflate(R.layout.row_list_all_client_order, parent, false);

            vi.requestLayout();

            /******** View Holder Object to contain tabitem.xml file elements ************/
            holder = new ViewHolder();

            holder.txtOrderPname = (TextView) vi.findViewById(R.id.txtOrderPName);
            holder.txtOrderSName = (TextView) vi.findViewById(R.id.txtOrderSName);
            holder.txtOrderSAddress = (TextView) vi.findViewById(R.id.txtOrderSAddress);
            holder.txtOrderState = (TextView) vi.findViewById(R.id.txtOrderState);
            holder.txtOrderPCode = (TextView) vi.findViewById(R.id.txtOrderPCode);

            holder.imgOrderPhone = (CircleImageView) vi.findViewById(R.id.imgOrderPhone);
            holder.imgOrderProduct = (CircleImageView) vi.findViewById(R.id.imgOrderProduct);
            holder.imgOrderShop = (CircleImageView) vi.findViewById(R.id.imgOrderShop);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.txtOrderPname.setText("No Data");

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (setwget) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtOrderPname.setText("Product Name : " + tempValues.getOrderPName());
            holder.txtOrderSName.setText("Shop Name : " + tempValues.getOrderSName());
            holder.txtOrderSAddress.setText(tempValues.getOrderSAddress());
            holder.txtOrderState.setText(tempValues.getOrderState());
            holder.txtOrderPCode.setText("Order Code : " + tempValues.getOrderPCode());

            /*Picasso.with(activity).load(tempValues.getOrderP()).error(R.drawable.logo_home)
                    .placeholder(R.drawable.logo_home).into(holder.imgOrderProduct);*/

            Glide.with(activity).load(tempValues.getOrderP())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.imgOrderProduct);

            /*Picasso.with(activity).load(tempValues.getOrderS()).error(R.drawable.logo_home)
                    .placeholder(R.drawable.logo_home).into(holder.imgOrderShop);*/

            Glide.with(activity).load(tempValues.getOrderS())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.imgOrderShop);

            holder.imgOrderPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shop.onPhoneClick(position);
                }
            });

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