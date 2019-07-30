package com.dasta.eemapp.adapter.Client;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Client.VideoProfile;
import com.dasta.eemapp.helper.setwget;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Mohamed on 06/11/2015.
 */
public class Adapter_CustomList_All_Client_Video extends RecyclerView.Adapter<Adapter_CustomList_All_Client_Video.ViewHolder> {

    private VideoProfile shop;
    private Activity activity;
    private LayoutInflater inflater;
    public Resources res;
    int i = 0;
    setwget tempValues = null;
    ArrayList data;

    public Adapter_CustomList_All_Client_Video(Activity a, ArrayList d, Resources resLocal, VideoProfile shop1) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;
        shop = shop1;


        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_video, parent, false);
        ViewHolder gvh = new ViewHolder(groceryProductView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (data.size() <= 0) {
            holder.imgClientVideo.setImageResource(R.drawable.logo_home);

        } else {
            tempValues = null;
            tempValues = (setwget) data.get(position);


            holder.imgClientVideo.setImageResource(R.drawable.logo_home);

            holder.imgClientVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.size() <= 0) {
                        Toast.makeText(activity, "No Data", Toast.LENGTH_LONG).show();
                    } else {
                        //shop.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imgClientVideo;

        public ViewHolder(View view) {
            super(view);
            imgClientVideo = (CircleImageView) view.findViewById(R.id.imgClientVideo);
        }
    }

}