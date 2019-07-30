package com.dasta.eemapp.adapter.Shop;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Shop.Order;
import com.dasta.eemapp.fragment.Client.ClientOrder;
import com.dasta.eemapp.helper.setwget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Mohamed on 06/11/2015.
 */
public class Adapter_CustomList_All_Shop_Order extends BaseAdapter implements View.OnClickListener {

    private Order activity;
    private static LayoutInflater inflater = null;
    public Resources res;
    int i = 0;
    setwget tempValues = null;
    ArrayList data;


    public Adapter_CustomList_All_Shop_Order(Order a, ArrayList d, Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;


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

        public CircleImageView imgOrderProduct, imgOrderPhone;
        public TextView txtOrderPname, txtOrderSName, txtOrderState, txtOrderPCode,
                txtOrderQuantity, txtOrderPrice;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /********** Inflate tabitem.xml file for each row ( Defined below ) ************/
            vi = inflater.inflate(R.layout.row_list_all_shop_order, parent, false);

            vi.requestLayout();

            /******** View Holder Object to contain tabitem.xml file elements ************/
            holder = new ViewHolder();

            holder.txtOrderPname = (TextView) vi.findViewById(R.id.txtSOrderPName);
            holder.txtOrderSName = (TextView) vi.findViewById(R.id.txtSOrderSName);
            holder.txtOrderState = (TextView) vi.findViewById(R.id.txtSOrderState);
            holder.txtOrderPCode = (TextView) vi.findViewById(R.id.txtSOrderPCode);
            holder.txtOrderQuantity = (TextView) vi.findViewById(R.id.txtOrderQuantity);
            holder.txtOrderPrice = (TextView) vi.findViewById(R.id.txtSOrderPrice);

            holder.imgOrderPhone = (CircleImageView) vi.findViewById(R.id.imgSOrderPhone);
            holder.imgOrderProduct = (CircleImageView) vi.findViewById(R.id.imgSOrderProduct);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.txtOrderPname.setText(R.string.noData);

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (setwget) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtOrderPname.setText("اسم المنتج : " + tempValues.getOrderPName());
            holder.txtOrderSName.setText("اسم العميل : " + tempValues.getOrderSName());
            holder.txtOrderState.setText(tempValues.getOrderState());
            holder.txtOrderQuantity.setText("الكميه : " + tempValues.getQuantity());
            holder.txtOrderPCode.setText("كود الحجز : " + tempValues.getOrderPCode());
            holder.txtOrderPrice.setText("السعر الكلى : " + tempValues.getPrice());

            Picasso.with(activity).load(tempValues.getOrderP()).error(R.drawable.logo_home)
                    .placeholder(R.drawable.logo_home).into(holder.imgOrderProduct);

            holder.imgOrderPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onPhoneClick(position);
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
                Toast.makeText(activity, R.string.noData, Toast.LENGTH_LONG).show();
            } else {
                activity.onItemClick(mPosition);
            }
        }
    }

}