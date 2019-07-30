package com.dasta.eemapp.fragment.Client;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.ImageLoaderTask;
import com.dasta.eemapp.PLManager;
import com.dasta.eemapp.PanoramaActivity;
import com.dasta.eemapp.R;
import com.dasta.eemapp.ShowPanoramaActivity;
import com.dasta.eemapp.activity.Client.Home;
import com.dasta.eemapp.activity.Client.Home_Shop;
import com.dasta.eemapp.activity.Client.Login;
import com.dasta.eemapp.activity.MapActivity;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.Client.SessionManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;

import com.facebook.share.widget.ShareDialog;
import com.facebook.share.model.ShareLinkContent;
import com.panoramagl.PLICamera;
import com.panoramagl.PLImage;
import com.panoramagl.PLSphericalPanorama;
import com.panoramagl.structs.PLRange;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
public class ShopProfile extends Fragment {

    View v;
    ImageView imgClientShopProfile;
    //CircleImageView imgClientShopMap, imgClientShopPhone;
    TextView txtShopProfileAddress;
    FrameLayout llShopClientProduct, llShopClientOffer, llShopClientChat, llShopClientVideo,llAddress,llPhone,llMap;
    public static String title, logoUrl, phone, address, id, lat, lng, cat, city;
    public static int flag = 0;
    private SessionManager session;

    //Share Views
    FloatingActionButton shareFabBtn;
    BottomSheetLayout bottomSheetLayout;
    Bitmap sharedBitmap;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    ImageView shopClosedTxt;
    FrameLayout shopFrame;
    public static Bitmap sharedBitmapFace;
    private PLManager plManager;
    String imageName;

    //Panorama Images
    //private GyroscopeObserver gyroscopeObserver;
    //PanoramaImageView panoramaImageView;
    ImageView scaleIc;
    public static boolean isFullScreen=false;

    //private VrPanoramaView panoWidgetView;
    private ImageLoaderTask backgroundImageLoaderTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         v = inflater.inflate(R.layout.fragment_client_shop_profile, null);

         shopClosedTxt=v.findViewById(R.id.shop_closeTxt);
         shopFrame=v.findViewById(R.id.shop_frame);

      //  panoWidgetView = (VrPanoramaView) v.findViewById(R.id.pano_view);
        llAddress=v.findViewById(R.id.addressLinear);
        llPhone=v.findViewById(R.id.phoneLinear);
        llMap=v.findViewById(R.id.map_linear);

        // Initialize GyroscopeObserver.
       /* gyroscopeObserver = new GyroscopeObserver();
        // Set the maximum radian the device should rotate to show image's bounds.
        // It should be set between 0 and π/2.
        // The default value is π/9.
        gyroscopeObserver.setMaxRotateRadian(Math.PI/9);
        panoramaImageView = (PanoramaImageView) v.findViewById(R.id.panorama_image_view);
        panoramaImageView.setEnablePanoramaMode(true);
        panoramaImageView.setEnableScrollbar(true);

        // Set GyroscopeObserver for PanoramaImageView.
        panoramaImageView.setGyroscopeObserver(gyroscopeObserver);*/

        scaleIc=v.findViewById(R.id.scalePanoramaIc);

        //Set On Scale Icon click
        onScaleIconClick();

        flag = 1;

        Shop.flag = 0;
        DepartmentProfile.flag = 0;
        MapProfile.flag = 0;
        OfferProfile.flag = 0;
        ProductProfile.flag = 0;
        SingleProduct.flag = 0;

        //Home_Shop.imgClientShopUser.setVisibility(View.INVISIBLE);
        //Home_Shop.spCategories.setVisibility(View.INVISIBLE);

        // Session manager
        session = new SessionManager(getActivity());


        if (Home_Shop.keySearch == 1) {
            cat = "";
            city = "";
        } else if (Home_Shop.keySearch == 2) {
            cat = "";
            city = "";
        } else {
            cat = getArguments().getString("shop_client_cat");
            city = getArguments().getString("shop_client_city");
        }

        title = getArguments().getString("shop_client_title");

        Home_Shop.txtTitle.setVisibility(View.GONE);
        Home_Shop.barTitle.setVisibility(View.VISIBLE);
        Home_Shop.barTitle.setText(title);

        logoUrl = getArguments().getString("shop_client_img_url");

        phone = getArguments().getString("shop_client_phone");

        address = getArguments().getString("shop_client_address");

        id = getArguments().getString("shop_client_id");

        lat = getArguments().getString("shop_client_lat");


        lng = getArguments().getString("shop_client_lng");

       // Home_Shop.txtClientShopTitle.setText(title);

        //Check Shop State
        checkShopState();

        txtShopProfileAddress = (TextView) v.findViewById(R.id.txtClientShopProfileAddress);
        txtShopProfileAddress.setText(address);

        imgClientShopProfile = (ImageView) v.findViewById(R.id.imgClientShopProfileLogo);
        Glide.with(getActivity()).load(logoUrl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgClientShopProfile);

        llMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat != null) {
                   /* FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    MapProfile mapProfile = new MapProfile();
                    Bundle bundle = new Bundle();
                    bundle.putString("shop_profile_map_lat", lat);
                    bundle.putString("shop_profile_map_lng", lng);
                    mapProfile.setArguments(bundle);
                    ft.replace(R.id.fClientShop, mapProfile);
                    ft.commit();*/

                    /*Intent intent=new Intent(getActivity(), MapActivity.class);

                    intent.putExtra("shop_profile_map_lat", Double.parseDouble(lat));
                    intent.putExtra("shop_profile_map_lng", Double.parseDouble(lng));
                    startActivity(intent);*/

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr="+Double.parseDouble(lat)+","+Double.parseDouble(lng)));
                    startActivity(intent);

                }else{
                    Toast.makeText(getActivity(), "غير متاح", Toast.LENGTH_LONG).show();
                }
            }
        });

        llPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                        getActivity());
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.select_dialog_item);
                arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

                arrayAdapter.add(phone);

                builderSingle.setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener() {

                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                        arrayAdapter.getItem(which).toString()));
                                ShopProfile.this.startActivity(i);
                            }
                        });
                builderSingle.show();
            }
        });

        llShopClientProduct =  v.findViewById(R.id.llClientShopProduct);
        llShopClientProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("shop_department_profile_id", id);
                DepartmentProfile departmentProfile = new DepartmentProfile();
                departmentProfile.setArguments(bundle);
                ft.replace(R.id.fClientShop, departmentProfile);
                ft.commit();
            }
        });

        llShopClientOffer =  v.findViewById(R.id.llClientShopOffer);
        llShopClientOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("shop_offer_profile_id", id);
                OfferProfile offerProfile = new OfferProfile();
                offerProfile.setArguments(bundle);
                ft.replace(R.id.fClientShop, offerProfile);
                ft.commit();
            }
        });

        llShopClientChat =  v.findViewById(R.id.llClientShopChat);
        llShopClientChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user is already logged in or not
                if (!(session.isLoggedIn())) {
                    // custom dialog

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_ok_cancel);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));


                    //Get dialog views
                    Button okBtn=(Button) dialog.findViewById(R.id.ok);
                    Button cancelBtn=(Button) dialog.findViewById(R.id.cancel);

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }

                    });
                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getActivity(), Login.class);
                            intent.putExtra("calling_activity","shop_profile");
                            intent.putExtra("view_shop_id", id);
                            intent.putExtra("view_shop_address", address);
                            intent.putExtra("view_shop_phone", phone);
                            intent.putExtra("view_shop_name", title);
                            intent.putExtra("view_shop_img", logoUrl);
                            intent.putExtra("view_shop_lat", lat);
                            intent.putExtra("view_shop_lng", lng);
                            intent.putExtra("shop_client_cat",cat);
                            intent.putExtra("shop_client_city",city);
                            dialog.dismiss();
                            startActivity(intent);
                        }
                    });

                    dialog.show();

                } else {
                    //Toast.makeText(getActivity(), UserProfile.user_id, Toast.LENGTH_LONG).show();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("shop_chat_receiver_id", id);
                    bundle.putString("shop_chat_receiver_name", title);
                    ChatRoom offerProfile = new ChatRoom();
                    offerProfile.setArguments(bundle);
                    ft.replace(R.id.fClientShop, offerProfile);
                    ft.commit();
                }
            }
        });

        llShopClientVideo =  v.findViewById(R.id.llClientShopVideo);
        llShopClientVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("shop_video_profile_id", id);
                VideoProfile videoProfile = new VideoProfile();
                videoProfile.setArguments(bundle);
                ft.replace(R.id.fClientShop, videoProfile);
                ft.commit();*/
                Intent intent=new Intent(getActivity(), PanoramaActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        //Set Panorama Gl
        plManager =new PLManager(Home_Shop.context);
        plManager.setContentView((ViewGroup)v.findViewById(R.id.pano_view));
        plManager.onCreate();

//        plManager.setInertiaEnabled(true);
        plManager.setZoomEnabled(true);
//        plManager.setMinDistanceToEnableScrolling(10);
        plManager.startSensorialRotation();

        //You can use accelerometer
        plManager.setAccelerometerEnabled(false);
        plManager.setAccelerometerLeftRightEnabled(true);
        plManager.setAccelerometerUpDownEnabled(false);
        plManager.setScrollingEnabled(true);
        plManager.setInertiaEnabled(true);
        plManager.setMinDistanceToEnableScrolling(2);


        //setFovRange determines Zoom range. Range values from -1.0f to 1.0f
        plManager.getCamera().setFovRange(PLRange.PLRangeMake(0.0f, 1.0f));
        //Example with Sphere type (you need one image)
//        plManager.setType(PLViewType.PLViewTypeSpherical);
        //plManager.setValidForScrolling(true);
        //plManager.setAccelerometerEnabled(false);
//        plManager.startInertia();
//        plManager.setScrollingEnabled(true);

//        plManager.setScrollingEnabled(true);
//        plManager.startAnimation();

//        plManager.setAccelerometerLeftRightEnabled(false);
//        plManager.setAccelerometerEnabled(false);
//        plManager.setAccelerometerUpDownEnabled(false);
//
//        plManager.setAccelerometerLeftRightEnabled(true);
//        plManager.setAccelerometerSensitivity((float) 20.1);

        // pass in the name of the image to load from assets.

        v.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                    //do something
                    plManager.onTouchEvent(event);

                return true;
            }
        });

        RetrieveFeedTask retrieveFeedTask=new RetrieveFeedTask();
        retrieveFeedTask.execute("http://dasta.net/data/eem/Upload/Shop/Image/".concat(id).concat("_p.png"));

        //Set Up Share the Shop
        setUpShareTheShop();



        return v;
    }

    private void checkShopState() {
        StringRequest shopStateRequest=new StringRequest(Request.Method.POST, "http://dasta.net/data/eem/Shop/get_shop_status.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("Shop State",response);
                try {
                    JSONObject shopState=new JSONObject(response);
                    if (shopState.getInt("active")==1){
                        shopClosedTxt.setVisibility(View.GONE);
                    }else {
                        shopFrame.setVisibility(View.INVISIBLE);
                        shareFabBtn.setVisibility(View.INVISIBLE);
                    }
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
                HashMap<String,String> map=new HashMap<>();
                map.put("id",id);
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(shopStateRequest);
    }


    private void onScaleIconClick() {
        scaleIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),ShowPanoramaActivity.class);
                intent.putExtra("img",id.concat("_p.png"));
                startActivity(intent);

                return;
//                if (isFullScreen){
//                    isFullScreen=false;
//                    scaleIc.setImageDrawable(getResources().getDrawable(R.drawable.common_full_open_on_phone));
//                    scaleIc.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit_24dp));
//
//                    llAddress.animate().translationYBy(2000).setDuration(1000).start();
//
//                    llPhone.animate().translationXBy(-2000).setDuration(1000).start();
//                    llShopClientVideo.animate().translationXBy(-2000).setDuration(1000).start();
//                    llShopClientOffer.animate().translationXBy(-2000).setDuration(1000).start();
//
//                    llMap.animate().translationXBy(2000).setDuration(1000).start();
//                    llShopClientProduct.animate().translationXBy(2000).setDuration(1000).start();
//                    llShopClientChat.animate().translationXBy(2000).setDuration(1000).start();
//
//                    shareFabBtn.animate().translationYBy(-2000).setDuration(1000).start();
//
//
//                }else {
//                    isFullScreen=true;
//                    scaleIc.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit_24dp));
//
//                    llAddress.animate().translationYBy(-2000).setDuration(1000).start();
//
//                    llPhone.animate().translationXBy(2000).setDuration(1000).start();
//                    llShopClientVideo.animate().translationXBy(2000).setDuration(1000).start();
//                    llShopClientOffer.animate().translationXBy(2000).setDuration(1000).start();
//
//                    llMap.animate().translationXBy(-2000).setDuration(1000).start();
//                    llShopClientProduct.animate().translationXBy(-2000).setDuration(1000).start();
//                    llShopClientChat.animate().translationXBy(-2000).setDuration(1000).start();
//
//                    shareFabBtn.animate().translationYBy(2000).setDuration(1000).start();
//
//                }
            }
        });
    }

    private void setUpShareTheShop() {

        FacebookSdk.sdkInitialize(getActivity());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        bottomSheetLayout =(BottomSheetLayout) v.findViewById(R.id.client_home_bottomsheet);
        shareFabBtn=(FloatingActionButton) v.findViewById(R.id.shopProfileShare);

        shareFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgClientShopProfile.setDrawingCacheEnabled(true);
                imgClientShopProfile.buildDrawingCache();
                sharedBitmap= imgClientShopProfile.getDrawingCache();

                handleShareLink();

                imgClientShopProfile.setDrawingCacheEnabled(false); // clear drawing cache
            }
        });

    }

    private void handleShareLink() {

        //Share
        //Share Intent for Text Only
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"http://dasta.net/data/eem/Client/redirect_shop_profile.php/".
                concat(id));
        shareIntent.setType("text/plain");

        //Share Intent for Text and Image
        if (Utility.checkPermissionREAD_EXTERNAL_STORAGE(getActivity())&&Utility.checkPermissionWRITE_EXTERNAL_STORAGE(getActivity())){

            if (lat == null||lat.equals("")||lng == null||lng.equals("")){
                lng="null";
                lat="null";
            }
            final Intent imgShareIntent = new Intent(Intent.ACTION_SEND);
            imgShareIntent.setType("image/*");
            imgShareIntent.putExtra(Intent.EXTRA_STREAM,Utility.getImageUri(getActivity(), sharedBitmap));
            imgShareIntent.putExtra(Intent.EXTRA_TEXT,"http://dasta.net/data/eem/Client/redirect_shop_profile.php/".
                    concat(id));

            IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(getActivity(), imgShareIntent, "مشاركة...",
                    new IntentPickerSheetView.OnIntentPickedListener() {
                        @Override
                        public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo) {
                            bottomSheetLayout.dismissSheet();
                            if (activityInfo.componentName.getPackageName().equals("com.facebook.orca")) {
                                final Intent imgShareIntent2 = new Intent(Intent.ACTION_SEND);
                                imgShareIntent2.setType("text/plain");
                                imgShareIntent2.putExtra(Intent.EXTRA_TEXT,"http://dasta.net/data/eem/Client/redirect_shop_profile.php/".
                                        concat(id));
                                startActivity(activityInfo.getConcreteIntent(imgShareIntent2));
                            }else if (activityInfo.componentName.getPackageName().equals("com.facebook.katana")){

                                    //startActivity(activityInfo.getConcreteIntent(imgShareIntent));



                                /*SharePhoto photo = new SharePhoto.Builder()
                                        .setBitmap(sharedBitmap)
                                        .build();
                                SharePhotoContent photoContent = new SharePhotoContent.Builder()
                                        .addPhoto(photo)
                                        .build();



                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.Slack&hl=en"))
                                            .setShareHashtag(new ShareHashtag.Builder()
                                                    .setHashtag("#EEM_App")
                                                    .build()).setQuote("Connect on a global scale.")
                                            .setContentDescription("Welcome To EEM App").setImageUrl(Utility.getImageUri(Home.this, sharedBitmap))
                                            .build();

                                    shareDialog.show(linkContent);
                                }*/

//                                final Intent imgShareIntent2 = new Intent(Intent.ACTION_SEND);
//                                imgShareIntent2.setType("text/plain");
//                                imgShareIntent2.putExtra(Intent.EXTRA_TEXT,"http://dasta.net/data/eem/Client/redirect_shop_profile.php/".
//                                        concat(id));
//                                startActivity(activityInfo.getConcreteIntent(imgShareIntent2));



                                ShareLinkContent content = new ShareLinkContent.Builder()
                                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.dasta.eemapp"))
                                        .setQuote(title.concat(" - ").concat(cat).concat(" - ").concat(city).concat(" ").concat(address)
                                        .concat(" - ").concat(phone))
                                            .build();

//                                SharePhoto photo = new SharePhoto.Builder()
//                                        .setBitmap(sharedBitmapFace)
//                                        .build();
//                                SharePhotoContent content = new SharePhotoContent.Builder()
//                                        .addPhoto(photo)
//                                        .setRef("")
//                                        .build();


                                    shareDialog.show(content);



                                }else if (activityInfo.componentName.getPackageName().equals("com.twitter.android")){

                                    //Toast.makeText(Home.this,"Twitter Selected",Toast.LENGTH_SHORT).show();
                                    startActivity(activityInfo.getConcreteIntent(imgShareIntent));

                                }else{

                                    // Toast.makeText(Home.this,"FaceBook,Twitter Not Selected",Toast.LENGTH_SHORT).show();
                                    startActivity(activityInfo.getConcreteIntent(imgShareIntent));
                                }
                            }

                    });

            // Filter out built in sharing options such as bluetooth and beam.
            intentPickerSheet.setFilter(new IntentPickerSheetView.Filter() {

                @Override
                public boolean include(IntentPickerSheetView.ActivityInfo info) {
                    return !info.componentName.getPackageName().startsWith("com.android");
                }

            });
            // Sort activities in reverse order for no good reason
            intentPickerSheet.setSortMethod(new Comparator<IntentPickerSheetView.ActivityInfo>() {
                @Override
                public int compare(IntentPickerSheetView.ActivityInfo lhs, IntentPickerSheetView.ActivityInfo rhs) {
                    return rhs.label.compareTo(lhs.label);
                }
            });
            bottomSheetLayout.showWithSheetView(intentPickerSheet);

        }/*else {
            Utility.showDialog("السماح للتطبيق بحفظ الصور ف الذاكرة الخارجية ؟",Home.this,"android.permission.WRITE_EXTERNAL_STORAGE");
        }*/


    }

    private synchronized void loadPanoImage(String panoImageName) {
        ImageLoaderTask task = backgroundImageLoaderTask;
        if (task != null && !task.isCancelled()) {
            // Cancel any task from a previous loading.
            task.cancel(true);
        }

//        // pass in the name of the image to load from assets.
//        VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
//        viewOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER|VrPanoramaView.Options.TYPE_MONO;

        // create the task passing the widget view and call execute to start.
        task = new ImageLoaderTask(plManager,panoImageName);
        task.execute(getActivity().getAssets());
        backgroundImageLoaderTask = task;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Bitmap> {

        private Exception exception;

        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap feed) {
            if (feed!=null){
                PLSphericalPanorama panorama = new PLSphericalPanorama();
//                Bitmap b = BitmapFactory.decodeResource(getResources(), resourceIds[index]);

//        panorama.setImage(new PLImage(PLUtils.getBitmap(this, resourceIds[index]), false));
                panorama.setImage(new PLImage(Bitmap.createScaledBitmap(feed, 1024, 1024, false), false));

                float pitch = 5f;
                float yaw = 0f;
                float zoomFactor = 0.8f;

                PLICamera camera = plManager.getPanorama().getCamera();
                pitch = camera.getPitch();
                yaw = camera.getYaw();
                zoomFactor = camera.getZoomFactor();

                panorama.getCamera().setRotationSensitivity(97.0F);
                panorama.getCamera().lookAtAndZoomFactor(pitch, yaw, 0.65f, true);
                plManager.setPanorama(panorama);

                sharedBitmapFace=feed;

            }else {
//                RetrieveFeedTask retrieveFeedTask=new RetrieveFeedTask();
//                retrieveFeedTask.execute("http://dasta.net/data/eem/Upload/Shop/Image/eem_panorama360.jpg");
                loadPanoImage("eem_panorama360.jpg");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        plManager.onResume();
    }

    @Override
    public void onPause() {
        plManager.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        plManager.onDestroy();
        super.onDestroy();
    }
}
