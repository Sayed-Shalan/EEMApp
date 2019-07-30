package com.dasta.eemapp.fragment.Client;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.Chat;
import com.dasta.eemapp.helper.ChatListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    Context context;
    ArrayList<ChatListModel> data;
    LayoutInflater layoutInflater;

    public ChatListAdapter(Context context, ArrayList<ChatListModel> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatListViewHolder(layoutInflater.inflate(R.layout.row_list_all_chat,parent,false));
    }

    public ChatListModel getItem(int position){
        return data.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {

        holder.nameTxt.setText(getItem(position).isMine()?getItem(position).getReceiver_name():
                getItem(position).getSender_name());
        String message=getItem(position).getMessage();
        final int user_id;
        final int myId;
        user_id=getItem(position).isMine()?getItem(position).getReceiver_id():
                getItem(position).getSender_id();
        myId=getItem(position).isMine()?getItem(position).getSender_id():
                getItem(position).getReceiver_id();
        if (message.length()>20){
            message=message.substring(0,19).concat("...");
        }
        holder.msgTxt.setText(message);
        Chat chat=new Chat();
        chat.setDateTime(getItem(position).getDateTime());
        holder.dateTimeTxt.setText(Utility.getDateNotification(context,chat));

        if (getItem(position).isClient()){

            try {
            Glide.with(context).load("http://dasta.net/data/eem/Upload/Shop/Image/"+String.valueOf(user_id)+".png").asBitmap().
                    dontAnimate().
//                                placeholder(R.drawable.unknown_male).
                    /*error(R.drawable.unknown_male).*/into(new BitmapImageViewTarget(holder.profileImg) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            resource=Utility.getCircularBitmap(resource);
                           // resource=Utility.addBorderToCircularBitmap(resource,15,context.getResources().getColor(R.color.gray_f2));
                            holder.profileImg.setImageBitmap(resource);
                        }
                    });

        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (RuntimeException e){
                e.printStackTrace();
            }
        }else {
            try {
                Glide.with(context).load("http://dasta.net/data/eem/Upload/Shop/Image/"+String.valueOf(myId)+".png").asBitmap().
                        dontAnimate().
//                                placeholder(R.drawable.unknown_male).
                        /*error(R.drawable.unknown_male).*/into(new BitmapImageViewTarget(holder.profileImg) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                resource=Utility.getCircularBitmap(resource);
                             //   resource=Utility.addBorderToCircularBitmap(resource,15,context.getResources().getColor(R.color.gray_f2));
                                holder.profileImg.setImageBitmap(resource);
                            }
                        });

            }catch (IllegalStateException e){
                e.printStackTrace();
            }catch (RuntimeException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //Create View Holder Class
    public class ChatListViewHolder extends RecyclerView.ViewHolder{

        //Init Views
        TextView nameTxt;
        TextView msgTxt;
        TextView dateTimeTxt;
        ImageView profileImg;


        public ChatListViewHolder(View itemView) {
            super(itemView);
            nameTxt=itemView.findViewById(R.id.contactName);
            msgTxt=itemView.findViewById(R.id.lastMessage);
            dateTimeTxt=itemView.findViewById(R.id.lastMessageTime);
            profileImg=itemView.findViewById(R.id.contactProfileImage);
        }
    }

}
