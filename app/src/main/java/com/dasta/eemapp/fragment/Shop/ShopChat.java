package com.dasta.eemapp.fragment.Shop;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.adapter.Client.ChatRecyclerAdapter;
import com.dasta.eemapp.adapter.Client.ChatRecyclerAdapterr;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.Chat;
import com.dasta.eemapp.helper.ChatContract;
import com.dasta.eemapp.helper.ChatPresenter;
import com.dasta.eemapp.helper.Constants;
import com.dasta.eemapp.helper.PushNotificationEvent;
import com.dasta.eemapp.helper.Shop.SQLiteHandler;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 23/03/2018.
 */

public class ShopChat extends Fragment implements TextView.OnEditorActionListener {

    public static int flag = 0;
    public  static boolean active = false;
    public static String senderId, senderName, receiverId, receiverName, sender, receiver, chatId, msg;
    RequestQueue requestQueue;
    private static RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;
    static TextView noMessagesTxt;
    private FrameLayout sendBtn;
    private com.dasta.eemapp.helper.Shop.SQLiteHandler shopDB;
    private com.dasta.eemapp.helper.Shop.SessionManager shopSession;
    public static Activity activity;
    ArrayList<Chat> listChat;
    private static ChatRecyclerAdapter mChatRecyclerAdapter;
    JSONArray receiverTokensArr=null;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_chat_room, null);
        activity=getActivity();
        flag = 1;
        bindViews(view);

        init();

        //get User Token
        getUserToken(receiverId);


        //get Old Messages
        getOldMessages();

        //set send button on click
        onSendButtonClick();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (ChatRoom.active){
//            Toast.makeText(getActivity(),"CHAT ROOM IS OPENED",Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(getActivity(),"CHAT ROOM ISN'T OPENED",Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }

    private void bindViews(View view) {
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        listChat=new ArrayList<>();
        sendBtn= view.findViewById(R.id.etClientMessageSendBtn);
        mRecyclerViewChat.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,
                true));
        mETxtMessage = (EditText) view.findViewById(R.id.etClientMessage);
        noMessagesTxt=view.findViewById(R.id.no_messagesTxt);
        mChatRecyclerAdapter = new ChatRecyclerAdapter(listChat,activity);
        mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        mChatRecyclerAdapter.notifyDataSetChanged();
        mETxtMessage.setOnEditorActionListener(this);

    }

    private void init() {

        // SqLite database handler
        shopDB = new SQLiteHandler(getActivity());

        HashMap<String, String> shopData = shopDB.getUserDetails();
        senderId = shopData.get("shopid");
        senderName = shopData.get("shopname");
        sender = senderName + "_" + senderId;


        receiverId = getArguments().getString("shop_chat_receiver_id");
        receiverName = getArguments().getString("shop_chat_receiver_name");
        receiver = receiverName + "_" + receiverId;

    }

    //Get User Token
    private void getUserToken(final String receiverId) {

        StringRequest getReceiverToken=new StringRequest(Request.Method.POST, "http://dasta.net/data/eem/Chat/get_user_token.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RECEIVER TOKEN",response);
                try {
                    JSONObject obj=new JSONObject(response);
                    receiverTokensArr=obj.getJSONArray("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("user_id",receiverId);
                return params;
            }
        };

        requestQueue=Volley.newRequestQueue(getActivity());
        requestQueue.add(getReceiverToken);


    }

    //Get Old Messages Between User
    private void getOldMessages() {
        StringRequest getOldMessagesRequest=new StringRequest(Request.Method.POST, "http://dasta.net/data/eem/Chat/get_old_chat.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Old Chat Response : ",response);
                try {
                    JSONObject messagesObj=new JSONObject(response);
                    JSONArray messagesArr=messagesObj.getJSONArray("result");
                    if (messagesArr.length()<=0){
                        noMessagesTxt.setVisibility(View.VISIBLE);
                    }else {
                        noMessagesTxt.setVisibility(View.INVISIBLE);
                        for (int i=0;i<messagesArr.length();i++){
                            Chat chat=new Chat();
                            JSONObject mssgObj=messagesArr.getJSONObject(i);
                            chat.setSenderUid(String.valueOf(mssgObj.getInt("sender_id")));
                            chat.setSender(mssgObj.getString("sender_name"));
                            chat.setReceiverUid(String.valueOf(mssgObj.getInt("receiver_id")));
                            chat.setReceiver(mssgObj.getString("receiver_name"));
                            chat.setMessage(mssgObj.getString("message"));
                            chat.setDateTime(mssgObj.getString("dateTime"));

                            if (senderId.equals(chat.getSenderUid())){
                                chat.setMine(true);
                            }else {
                                chat.setMine(false);
                            }

                            mChatRecyclerAdapter.add(chat);

                            //chat.setSenderUid();
                        }
                        mRecyclerViewChat.smoothScrollToPosition(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    noMessagesTxt.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("sender_id",senderId);
                params.put("receiver_id",receiverId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(getOldMessagesRequest);
    }

    private void onSendButtonClick() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }


    private void sendMessage() {
        final String message = mETxtMessage.getText().toString();
        final String receiver1 = receiverName;
        final String receiverUid = receiver;
        final String sender1 = senderName;
        final String senderUid = sender;
        final long dateTimeLong=System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final String currentDateandTime = sdf.format(new Date());

        Chat chat = new Chat(sender1,
                receiver1,
                senderUid,
                receiverUid,
                message,dateTimeLong);

        chat.setMine(true);
        chat.setDateTime(currentDateandTime);
        if (noMessagesTxt.getVisibility()==View.VISIBLE){
            noMessagesTxt.setVisibility(View.INVISIBLE);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(0);
        mETxtMessage.setText("");



        StringRequest insertChatReq=new StringRequest(Request.Method.POST, "http://dasta.net/data/eem/Chat/insert_message.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Insert Message: ",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("sender_id",senderId);
                params.put("receiver_id",receiverId);
                params.put("sender_name",sender1);
                params.put("receiver_name",receiver1);
                params.put("message",message);
                params.put("dateTime",currentDateandTime);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(insertChatReq);

        if (receiverTokensArr!=null){
            for (int i=0;i<receiverTokensArr.length();i++){
                try {
                    Utility.sendMessageNotification(senderId,sender1,receiverId,receiver1,message,currentDateandTime,"stc",
                            receiverTokensArr.getJSONObject(i).getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }

    }

    public static void addChatItem(Chat chat){
        if (noMessagesTxt.getVisibility()==View.VISIBLE){
            noMessagesTxt.setVisibility(View.INVISIBLE);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(0);
    }

}
