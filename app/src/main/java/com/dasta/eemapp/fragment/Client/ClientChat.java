package com.dasta.eemapp.fragment.Client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import com.dasta.eemapp.RecyclerViewTouchListner;
import com.dasta.eemapp.activity.Client.Home_Shop;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Client_Chat;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.ChatListModel;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.setwget;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientChat extends Fragment {

    public static int flag = 0;

    RecyclerView lstClientChat;
    ChatListAdapter chatListAdapter;
    ArrayList<ChatListModel> chatList;

    Resources res;
    RequestQueue requestQueue;
    private ProgressDialog pDialog;
    SQLiteHandler db;
    String userid, username;
    TextView noMsgTxt;
    ArrayList<String> clientsIDSList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_client_chat, null);

        flag = 1;

        lstClientChat =  v.findViewById(R.id.lstClientChat);
        noMsgTxt=v.findViewById(R.id.no_messages);
        clientsIDSList=new ArrayList<>();


        db = new SQLiteHandler(getActivity());

        final HashMap<String, String> user = db.getUserDetails();
        userid = user.get("userid");
        username = user.get("username");

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("جاري التحميل ..");
        pDialog.show();

        requestQueue = Volley.newRequestQueue(getActivity());

        res = getResources();
        chatList=new ArrayList<>();


        if (isNetworkAvailable()) {
        StringRequest getOldChatReq=new StringRequest(Request.Method.POST, "http://dasta.net/data/eem/Chat/get_chat_list.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resObj=new JSONObject(response);
                            JSONArray arr=resObj.getJSONArray("result");
                            if (arr.length()<=0){
                                noMsgTxt.setVisibility(View.VISIBLE);
                            }else {
                                for (int i=0;i<arr.length();i++){
                                    JSONObject obj=arr.getJSONObject(i);
                                    ChatListModel chatListModel=new ChatListModel();
                                    chatListModel.setClient(true);
                                    String clientId;
                                    boolean exists=false;
                                    if (userid.equals(String.valueOf(obj.getInt("sender_id")))){
                                        clientId=String.valueOf(obj.getInt("receiver_id"));
                                        chatListModel.setMine(true);
                                    }else {
                                        clientId=String.valueOf(obj.getInt("sender_id"));
                                        chatListModel.setMine(false);
                                    }

                                    for (int j=0;j<clientsIDSList.size();j++){
                                        if (clientsIDSList.get(j).equals(clientId)){
                                            exists=true;
                                        }
                                    }

                                    if (exists){
                                        continue;
                                    }else {
                                        clientsIDSList.add(clientId);
                                    }

                                    chatListModel.setSender_id(obj.getInt("sender_id"));
                                    chatListModel.setReceiver_id(obj.getInt("receiver_id"));
                                    chatListModel.setMessage(obj.getString("message"));
                                    chatListModel.setDateTime(obj.getString("dateTime"));
                                    chatListModel.setSender_name(obj.getString("sender_name"));
                                    chatListModel.setReceiver_name(obj.getString("receiver_name"));
                                    chatList.add(chatListModel);



                                }

                                lstClientChat.setLayoutManager(new LinearLayoutManager(getActivity()));
                                chatListAdapter=new ChatListAdapter(getActivity(),chatList);
                                lstClientChat.setAdapter(chatListAdapter);
                                chatListAdapter.notifyDataSetChanged();
                                hidePDialog();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            noMsgTxt.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<>();
                map.put("user_id",userid);
                return map;
            }
        };
            AppController.getInstance().addToRequestQueue(getOldChatReq);

            lstClientChat.addOnItemTouchListener(new RecyclerViewTouchListner(getActivity(), lstClientChat, new RecyclerViewTouchListner.recyclerViewTouchListner() {
                @Override
                public void onclick(View child, int position) {
                    flag = 0;
                    Bundle bundle = getArguments();
                    String receiverNameStr,receiverIdStr;
                    if (userid.equals(String.valueOf(chatList.get(position).getSender_id()))){
                        receiverIdStr=String.valueOf(chatList.get(position).getReceiver_id());
                        receiverNameStr=chatList.get(position).getReceiver_name();
                    }else {
                        receiverIdStr=String.valueOf(chatList.get(position).getSender_id());
                        receiverNameStr=chatList.get(position).getSender_name();
                    }

                    if (bundle!=null){
                        if (bundle.getBoolean("home",false)){
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ChatRoom clientChatRoom = new ChatRoom();
                            bundle.putString("shop_chat_receiver_id", receiverIdStr);
                            bundle.putString("shop_chat_receiver_name",receiverNameStr);
                            clientChatRoom.setArguments(bundle);
                            ft.replace(R.id.fClientHomeLayout, clientChatRoom);
                            ft.commit();



                        }else {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ChatRoom clientChatRoom = new ChatRoom();
                            bundle.putString("shop_chat_receiver_id", receiverIdStr);
                            bundle.putString("shop_chat_receiver_name",receiverNameStr);
                            clientChatRoom.setArguments(bundle);
                            ft.replace(R.id.fClientShop, clientChatRoom);
                            ft.commit();

                        }
                    }else {
                        bundle=new Bundle();

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ChatRoom clientChatRoom = new ChatRoom();
                        bundle.putString("shop_chat_receiver_id",receiverIdStr);
                        bundle.putString("shop_chat_receiver_name",receiverNameStr);
                        clientChatRoom.setArguments(bundle);
                        ft.replace(R.id.fClientHomeLayout, clientChatRoom);
                        ft.commit();

                    }



                }

                @Override
                public void onLongClick(View child, int position) {

                }
            }));

        } else {
            Toast.makeText(getActivity(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
            hidePDialog();
        }


        return v;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
