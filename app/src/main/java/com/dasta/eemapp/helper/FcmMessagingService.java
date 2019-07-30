package com.dasta.eemapp.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Home_Shop;
import com.dasta.eemapp.activity.Splash;
import com.dasta.eemapp.fragment.Client.ChatRoom;
import com.dasta.eemapp.fragment.Shop.ShopChat;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.Client.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Mohamed on 24/03/2018.
 */

public class FcmMessagingService extends FirebaseMessagingService {
    String type = "";
    public static Context context;
    private SessionManager clientSession;
    private SQLiteHandler clientDB;
    private com.dasta.eemapp.helper.Shop.SQLiteHandler shopDB;
    private com.dasta.eemapp.helper.Shop.SessionManager shopSession;
    RequestQueue requestQueue;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        context=this;

        // SQLite database handler
        clientDB = new SQLiteHandler(getApplicationContext());
        shopDB = new com.dasta.eemapp.helper.Shop.SQLiteHandler(getApplicationContext());
        // Session manager
        clientSession = new SessionManager(getApplicationContext());
        shopSession = new com.dasta.eemapp.helper.Shop.SessionManager(getApplicationContext());

        HashMap<String, String> clientData = clientDB.getUserDetails();
        HashMap<String, String> shopData = shopDB.getUserDetails();

       if (remoteMessage.getData().get("type").equals("cts")){
           Log.e("MSG TYPE","CTS");
           if(shopSession.isLoggedIn()){
               if(shopDB.getUserDetails().get("shopid").equals(
                       remoteMessage.getData().get("receiver_id"))){

                   //Check (Shop Chat) Fragment Visibility
                   if (ShopChat.active){

                       if (ShopChat.receiverId.equals(remoteMessage.getData().get("sender_id"))){

                           final Chat chat = new Chat(remoteMessage.getData().get("sender_name"),
                                   remoteMessage.getData().get("receiver_name"),
                                   remoteMessage.getData().get("sender_id"),
                                   remoteMessage.getData().get("receiver_id"),
                                   remoteMessage.getData().get("msg"),5);
                           chat.setMine(false);
                           chat.setDateTime(remoteMessage.getData().get("dateTime"));
                           new Handler(Looper.getMainLooper()).post(new Runnable() {
                               public void run() {
                                   ShopChat.addChatItem(chat);
                               }
                           });


                       }else {
                           //Push Shop Notification
                           pushNotificationToShop(remoteMessage);
                       }

                   }else {
                       //Push Shop Notification
                       pushNotificationToShop(remoteMessage);
                   }

               }else {
                   Log.e("Msg Received","User Id Is Different");
               }
           }else{
               Log.e("Msg Received","Not Logged In");
           }
       }else {  //CLIENT
           Log.e("MSG TYPE","STC");
           if(clientSession.isLoggedIn()){
               if(clientDB.getUserDetails().get("userid").equals(
                       remoteMessage.getData().get("receiver_id"))){

                   //Check (Chat Room Client) Fragment Visibility
                   if (ChatRoom.active){
                       if (ChatRoom.receiverId.equals(remoteMessage.getData().get("sender_id"))){
//                           Toast.makeText(context,"VISIBLE : "+remoteMessage.getData().get("msg"),Toast.LENGTH_SHORT).show();
                           final Chat chat = new Chat(remoteMessage.getData().get("sender_name"),
                                   remoteMessage.getData().get("receiver_name"),
                                   remoteMessage.getData().get("sender_id"),
                                   remoteMessage.getData().get("receiver_id"),
                                   remoteMessage.getData().get("msg"),5);
                           chat.setMine(false);
                           chat.setDateTime(remoteMessage.getData().get("dateTime"));
                           new Handler(Looper.getMainLooper()).post(new Runnable() {
                               public void run() {
                                   ChatRoom.addChatItem(chat);
                               }
                           });

                       }else {

                           //Push Shop Notification
                           pushNotificationToClient(remoteMessage);

                       }
//

                   }else {
//                       Toast.makeText(context,"NOT VISIBLE : "+remoteMessage.getData().get("msg"),Toast.LENGTH_SHORT).show();
                       //Push Shop Notification
                       pushNotificationToClient(remoteMessage);

                   }

               }else {
                   Log.e("Msg Received","User Id Is Different");
               }
           }else{
               Log.e("Msg Received","Not Logged In");
           }
       }

    }

    private void pushNotificationToClient(RemoteMessage message) {


        Intent intent = new Intent(this,Home_Shop.class);
        intent.putExtra("open_chat",true);
        intent.putExtra("shop_chatt_sender_id",message.getData().get("receiver_id"));
        intent.putExtra("shop_chatt_sender_name",message.getData().get("receiver_name"));
        intent.putExtra("shop_chatt_receiver_id",message.getData().get("sender_id"));
        intent.putExtra("shop_chatt_receiver_name",message.getData().get("sender_name"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        // The id of the channel.
        String id = "my_channel_01";

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(message.getData().get("sender_name"))
                    .setContentText(message.getData().get("msg"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setLargeIcon(BitmapFactory.decodeResource(
                            getResources(), R.drawable.logo_home))
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setOngoing(true)
                    .setChannelId(id)
                    .setContentIntent(pendingIntent);
        }else {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(message.getData().get("sender_name"))
                    .setContentText(message.getData().get("msg"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setLargeIcon(BitmapFactory.decodeResource(
                            getResources(),R.drawable.logo_home))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// The user-visible name of the channel.
        CharSequence name ="channel_name";

// The user-visible description of the channel.
        String description = "user visible description";

        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(id, name,importance);

// Configure the notification channel.
        mChannel.setDescription(description);

        mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);

        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(mChannel);
        final int min = 20;
        final int max = 80;
        final int random = new Random().nextInt((max - min) + 1) + min;
        notificationManager.notify(random /* ID of notification */, notificationBuilder.build());
    }

//    private void sendNotification(String messageBody) {
//        String id = "", message = "", title = "";
//
//        if (type.equals("json")) {
//            try {
//                JSONObject jsonObject = new JSONObject(messageBody);
//                id = jsonObject.getString("id");
//                message = jsonObject.getString("message");
//                title = jsonObject.getString("title");
//            } catch (JSONException e) {
//                //
//            }
//        } else if (type.equals("message")) {
//            message = messageBody;
//        }
//
//        Intent intent = new Intent(this, Splash.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        NotificationCompat.Builder notificationBulider = new NotificationCompat.Builder(this);
//        notificationBulider.setContentTitle("eemapp");
//        notificationBulider.setContentText(message);
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        notificationBulider.setSound(soundUri);
//        notificationBulider.setSmallIcon(R.drawable.logo_home);
//        notificationBulider.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_home));
//        notificationBulider.setAutoCancel(true);
//        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
//        v.vibrate(1000);
//        notificationBulider.setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notificationBulider.build());
//
//    }

    private void pushNotificationToShop(RemoteMessage message){

        Intent intent = new Intent(this,com.dasta.eemapp.activity.Shop.Chat.class);
        intent.putExtra("open_chat",true);
        intent.putExtra("shop_chatt_sender_id",message.getData().get("receiver_id"));
        intent.putExtra("shop_chatt_sender_name",message.getData().get("receiver_name"));
        intent.putExtra("shop_chatt_receiver_id",message.getData().get("sender_id"));
        intent.putExtra("shop_chatt_receiver_name",message.getData().get("sender_name"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        // The id of the channel.
        String id = "my_channel_01";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(message.getData().get("sender_name"))
                    .setContentText(message.getData().get("msg"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setLargeIcon(BitmapFactory.decodeResource(
                            getResources(), R.drawable.logo_home))
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setOngoing(true)
                    .setChannelId(id)
                    .setContentIntent(pendingIntent);
        }else {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(message.getData().get("sender_name"))
                    .setContentText(message.getData().get("msg"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setLargeIcon(BitmapFactory.decodeResource(
                            getResources(),R.drawable.logo_home))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setChannelId(id)
                    .setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// The user-visible name of the channel.
        CharSequence name ="channel_name";

// The user-visible description of the channel.
        String description = "user visible description";

        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(id, name,importance);

// Configure the notification channel.
        mChannel.setDescription(description);

        mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);

        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(mChannel);
        final int min = 20;
        final int max = 80;
        final int random = new Random().nextInt((max - min) + 1) + min;
        notificationManager.notify(random /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        updateOldToken(s);
    }

    //add user FCM Token
    private void updateOldToken(final String s){
        final String[] token = {""};
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token[0] =instanceIdResult.getToken();
            }
        });
        StringRequest sendToken=new StringRequest(Request.Method.POST, "http://dasta.net/data/eem/Chat/update_token.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("update TOKEN: ",response);
                final String[] token = {""};
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        token[0] =instanceIdResult.getToken();
                    }
                });
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("token",token[0]).apply();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<>();
                map.put("old_token",PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token",""));


                map.put("new_token",s);
                return map;
            }
        };
        requestQueue=Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(sendToken);
    }

}
