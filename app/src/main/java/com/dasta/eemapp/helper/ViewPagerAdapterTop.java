package com.dasta.eemapp.helper;

import android.content.Context;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Shop;
import com.dasta.eemapp.fragment.Client.Home_Category;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohamed on 03/10/2017.
 */

public class ViewPagerAdapterTop extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Home_Category home_category;
    ArrayList data;
    setwget tempValues = null;

    public ViewPagerAdapterTop(Context context, ArrayList data, Home_Category home_category) {
        this.context = context;
        this.data = data;
        this.home_category = home_category;
    }

    @Override
    public int getCount() {
        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout_top, null);

        tempValues = null;
        tempValues = (setwget) data.get(position);

        ImageView imageView =  view.findViewById(R.id.imageViewTop);
        TextView txtTitle =  view.findViewById(R.id.txtTitleTop);

        TextView txtAddress =  view.findViewById(R.id.txtAddressTop);

        TextView txtPhone =  view.findViewById(R.id.txtPhoneTop);
        if (data.size() <= 0) {
            imageView.setImageResource(R.drawable.logo_home);
            txtTitle.setText("لا يوجد داتا");
        } else {
            Glide.with(context).load(tempValues.getImgurl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);

            txtTitle.setText(tempValues.getTitle());

            txtAddress.setText(tempValues.getAddress());

            txtPhone.setText(tempValues.getPhone());



            //imageView.setImageResource((Integer) data.get(position));
        }
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

        view.setOnClickListener(new OnItemClickListener(position));

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
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
                Toast.makeText(context, "لا يوجد بيانات..", Toast.LENGTH_LONG).show();
            } else {
                home_category.onItemClickTopView(mPosition);
            }
        }
    }
}
