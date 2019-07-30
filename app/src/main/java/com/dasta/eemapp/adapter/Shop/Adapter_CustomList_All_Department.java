package com.dasta.eemapp.adapter.Shop;

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

import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Shop.Department;
import com.dasta.eemapp.helper.setwget;

import java.util.ArrayList;


/**
 * Created by Mohamed on 06/11/2015.
 */
public class Adapter_CustomList_All_Department extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private Department depart;
    private static LayoutInflater inflater = null;
    public Resources res;
    int i = 0;
    setwget tempValues = null;
    ArrayList data;


    public Adapter_CustomList_All_Department(Activity a, ArrayList d, Resources resLocal, Department department) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;
        depart = department;


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

        public TextView txtName;
        public ImageView imgDel, imgEdit;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /********** Inflate tabitem.xml file for each row ( Defined below ) ************/
            vi = inflater.inflate(R.layout.row_list_all_department, parent, false);

            vi.requestLayout();

            /******** View Holder Object to contain tabitem.xml file elements ************/
            holder = new ViewHolder();

            holder.txtName = (TextView) vi.findViewById(R.id.txtDepartmentName);
            holder.imgDel = (ImageView) vi.findViewById(R.id.imgShopDepartmentDel);
            holder.imgEdit = (ImageView) vi.findViewById(R.id.imgShopDepartmentEdit);


            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.txtName.setText(R.string.noData);

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (setwget) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtName.setText(tempValues.getDepartment_name());

            holder.imgDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    depart.OnItemDel(position);
                }
            });

            holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    depart.OnItemEdit(position);
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
                depart.onItemClick(mPosition);
            }
        }
    }

}