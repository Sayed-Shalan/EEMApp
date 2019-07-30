package com.dasta.eemapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.dasta.eemapp.activity.Utility;

import java.util.ArrayList;

public class PanoramaImageAdapter extends RecyclerView.Adapter<PanoramaImageAdapter.PanoramaImageViewHolder>{

    //Declare Vars
    Context context;
    ArrayList<PanoramaModel> data;
    LayoutInflater layoutInflater;

    public PanoramaImageAdapter(Context context, ArrayList<PanoramaModel> data) {
        this.context = context;
        this.data = data;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public PanoramaImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PanoramaImageViewHolder(layoutInflater.inflate(R.layout.single_item_panorama_image,parent,false));
    }

    @Override
    public void onBindViewHolder(final PanoramaImageViewHolder holder, int position) {

        try {
            Glide.with(context).load("http://dasta.net/data/eem/Upload/Shop/Image/".
                    concat(getItem(position).getUrl()).concat(".png")).asBitmap().
                    dontAnimate().
//                                placeholder(R.drawable.unknown_male).
                    /*error(R.drawable.unknown_male).*/into(new BitmapImageViewTarget(holder.panoramaImg) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            resource=Utility.getCircularBitmap(resource);
                            resource=Utility.addBorderToCircularBitmap(resource,12,context.getResources().getColor(R.color.gray_f2));
                            holder.panoramaImg.setImageBitmap(resource);
                        }
                    });
        }catch (RuntimeException e){
            e.printStackTrace();
        }


        holder.departmentTxt.setText(getItem(position).getDepartmentName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public PanoramaModel getItem(int position){
        return data.get(position);
    }

    public void addItem(PanoramaModel item){
        data.add(item);
        notifyDataSetChanged();
    }


    //Create ViewHolder class
    public class PanoramaImageViewHolder extends RecyclerView.ViewHolder{
        ImageView panoramaImg;
        TextView departmentTxt;
        public PanoramaImageViewHolder(View itemView) {
            super(itemView);
            panoramaImg=itemView.findViewById(R.id.single_item_panorama_image);
            departmentTxt=itemView.findViewById(R.id.single_item_panorama_department);
        }
    }

}
