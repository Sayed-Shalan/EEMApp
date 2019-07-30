package com.dasta.eemapp.fragment.Client;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.adapter.Client.ChatRecyclerAdapter;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Chat;
import com.dasta.eemapp.helper.ChatContract;
import com.dasta.eemapp.helper.ChatPresenter;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.Constants;
import com.dasta.eemapp.helper.PushNotificationEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 30/03/2018.
 */

public class ClientChatRoom extends Fragment implements ChatContract.View, TextView.OnEditorActionListener {

    String senderId, senderName, receiverId, receiverName, sender, receiver, chatId, msg;
    private SQLiteHandler db;

    public static int flag = 0;

    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;
    ImageView sendBtn;

    private ProgressDialog mProgressDialog;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;

    public static ChatRoom newInstance(String receiver,
                                       String receiverUid,
                                       String firebaseToken) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_RECEIVER, receiver);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        ChatRoom fragment = new ChatRoom();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_client_profile_chat_room, null);

        flag = 1;

        bindViews(view);

        //Set on Button send click
        onSendButtonClick();

        return view;
    }

    private void onSendButtonClick() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void bindViews(View view) {
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_profile_chat);
        mETxtMessage = (EditText) view.findViewById(R.id.etClientProfileMessage);
        sendBtn=(ImageView) view.findViewById(R.id.etClientProfileMessageSendBtn);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {

        // SqLite database handler
        db = new SQLiteHandler(getActivity());

        // Fetching user details from sqlite
        final HashMap<String, String> user = db.getUserDetails();
        senderId = user.get("userid");
        senderName = user.get("username");
        sender = senderName + "_" + senderId;

        receiverId = getArguments().getString("client_chatt_receiver_id");
        receiverName = getArguments().getString("client_chatt_receiver_name");
        receiver = receiverName + "_" + receiverId;

        chatId = sender + "_" + receiver;

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage("Please Wait....");
        mProgressDialog.setIndeterminate(true);

        mETxtMessage.setOnEditorActionListener(this);

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(senderName, receiverName);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }

    private void sendMessage() {
        String message = mETxtMessage.getText().toString();
        String receiver1 = receiverName;
        String receiverUid = receiver;
        String sender1 = senderName;
        String senderUid = sender;
        String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender1,
                receiver1,
                senderUid,
                receiverUid,
                message,
                System.currentTimeMillis());
        mChatPresenter.sendMessage(getActivity().getApplicationContext(),
                chat,
                receiverFirebaseToken);
    }

    @Override
    public void onSendMessageSuccess() {
        mETxtMessage.setText("");
        addChat();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>(),getActivity());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(sender,
                    pushNotificationEvent.getUid());
        }
    }

    //
    private void addChat() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_NEW_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                        Log.d("Success", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                Log.d("Error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sender_id", senderId);
                params.put("receiver_id", receiverId);
                params.put("sender_name", senderName);
                params.put("receiver_name", receiverName);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }
}
