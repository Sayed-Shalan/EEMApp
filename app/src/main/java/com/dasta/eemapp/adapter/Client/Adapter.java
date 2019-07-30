package com.dasta.eemapp.adapter.Client;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Home_Shop;
import com.dasta.eemapp.fragment.Client.Home_Category;
import com.dasta.eemapp.helper.Client.App;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<App> mApps;
    private boolean mHorizontal;
    private boolean mPager;

    public Adapter(boolean horizontal, boolean pager, List<App> apps) {
        mHorizontal = horizontal;
        mApps = apps;
        mPager = pager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mPager) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_pager, parent, false));
        } else {
            return mHorizontal ? new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter, parent, false)) :
                    new ViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.adapter_vertical, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        App app = mApps.get(position);
        holder.imageView.setImageResource(app.getDrawable());
        holder.nameTextView.setText(app.getName());
//        holder.ratingTextView.setText(String.valueOf(app.getRating()));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView nameTextView;
//        public TextView ratingTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
//            ratingTextView = (TextView) itemView.findViewById(R.id.ratingTextView);
        }

        @Override
        public void onClick(View v) {
            Log.d("App", mApps.get(getAdapterPosition()).getName());
            Intent intent = new Intent(v.getContext(), Home_Shop.class);
            intent.putExtra("shop_cat_title", mApps.get(getAdapterPosition()).getName());
            v.getContext().startActivity(intent);
        }
    }
}

