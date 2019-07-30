package com.dasta.eemapp.helper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Client.Home_Category;
import com.dasta.eemapp.fragment.Client.Shop;

import java.util.ArrayList;

public class ViewPagerAdapterTopProductCat extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    ArrayList data;
    setwget tempValues = null;

    public ViewPagerAdapterTopProductCat(Context context, ArrayList data) {
        this.context = context;
        this.data = data;
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
        View view = layoutInflater.inflate(R.layout.custom_layout_top_product_cat, null);


        ImageView imageView = (ImageView) view.findViewById(R.id.imgTopProductCat);

        //TextView txtTitle = (TextView) view.findViewById(R.id.txtTopProductCat);
        if (data.size() <= 0) {
            imageView.setImageResource(R.drawable.offer1);

        } else {
            tempValues = null;
            tempValues = (setwget) data.get(position);

            Glide.with(context).load(tempValues.getImgurl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);

            //txtTitle.setText(tempValues.getTitle());
        }
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

}
