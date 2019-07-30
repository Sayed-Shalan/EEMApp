package com.dasta.eemapp.adapter.Client;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.helper.Chat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private List<Chat> mChats;
    private Context context;

    public ChatRecyclerAdapter(List<Chat> chats,Context context) {
        mChats = chats;
        this.context=context;
    }

    public void add(Chat chat) {
        if (mChats.size()==0){
            mChats.add(0,chat);
        }else {
            mChats.add(0,chat);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mChats.get(position).isMine()) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        String dateStr=Utility.getDateNotification(context,chat);
        String alphabet = chat.sender.substring(0,1);



                    myChatViewHolder.txtChatMessage.setText(chat.message);
                    myChatViewHolder.txtUserAlphabet.setText(alphabet);
                    myChatViewHolder.mDateTxt.setText(dateStr);
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        String dateStr=Utility.getDateNotification(context,chat);

        String alphabet = chat.sender.substring(0, 1);

        otherChatViewHolder.txtChatMessage.setText(chat.message);
        otherChatViewHolder.txtUserAlphabet.setText(alphabet);
        otherChatViewHolder.dateStr.setText(dateStr);
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChats.get(position).isMine()) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet,mDateTxt;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            mDateTxt= (TextView) itemView.findViewById(R.id.text_view_chat_messageDate);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet,dateStr;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            dateStr= (TextView) itemView.findViewById(R.id.text_view_chat_messageDate);
        }
    }
}
