package com.dasta.eemapp.fragment.Client;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Home_Shop;
import com.dasta.eemapp.activity.Client.Login;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.helper.Client.SessionManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;
import com.squareup.picasso.Picasso;

import java.util.Comparator;

public class AllSingleOffer extends Fragment {

    View v;
    ImageView imgSingleProduct;
    TextView txtSingleProduct,descTxt,priceTxt,percentageTxt,afterTxt;
    Button fabSingleProduct, fabOfferAllRequest, fabOfferAllDeliver;
    public static String id, img, price, title, desc, per, quantity, deliver, reservation;
    public static int flag = 0;
    int saleprice, price1, per1;
    SessionManager sessionManager;

    //Share Views
    FloatingActionButton shareFabBtn;
    BottomSheetLayout bottomSheetLayout;
    Bitmap sharedBitmap;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    public static Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_client_all_single_offer, null);

        flag = 1;
        Offer.flag = 0;
        bundle=getArguments();
        try {
            Home_Shop.txtTitle.setVisibility(View.VISIBLE);
            Home_Shop.barTitle.setVisibility(View.GONE);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        sessionManager = new SessionManager(getActivity());
        fabSingleProduct = (Button) v.findViewById(R.id.fabOfferAllReservation);
        fabOfferAllDeliver = (Button) v.findViewById(R.id.fabOfferAllDeliver);

        price = getArguments().getString("all_single_offer_price");
        title = getArguments().getString("all_single_offer_title");
        img = getArguments().getString("all_single_offer_img");
        desc = getArguments().getString("all_single_offer_desc");
        id = getArguments().getString("all_single_offer_id");
        per = getArguments().getString("all_single_offer_per");
        quantity = getArguments().getString("all_single_offer_quantity");
        deliver = getArguments().getString("all_single_offer_deliver");
        reservation = getArguments().getString("all_single_offer_reservation");

        price1 = Integer.parseInt(price);
        per1 = 100 - (Integer.parseInt(per));
        saleprice = (price1 * per1) / 100;

        imgSingleProduct = (ImageView) v.findViewById(R.id.imgSingleAllOffer);
//        Picasso.with(getActivity()).load(img).error(R.drawable.logo_home)
//                .placeholder(R.drawable.logo_home).into(imgSingleProduct);
        Glide.with(getActivity()).load(img)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgSingleProduct);

        txtSingleProduct = (TextView) v.findViewById(R.id.txtSingleAllOffer);
        descTxt=(TextView) v.findViewById(R.id.txtSingleAllOfferDesc);
        priceTxt=(TextView) v.findViewById(R.id.txtSingleAllOfferPrice);
        percentageTxt=(TextView) v.findViewById(R.id.txtSingleAllOfferPercentage);
        afterTxt=(TextView) v.findViewById(R.id.txtSingleAllOfferPriceAfter);

        txtSingleProduct.setText(title);
        descTxt.setText(desc);
        priceTxt.setText(price.concat(" EGP"));
        percentageTxt.setText(per.concat(" %"));
        afterTxt.setText(String.valueOf(saleprice).concat(" EGP"));


        if (Integer.parseInt(deliver) < 0) {


            fabOfferAllDeliver.setVisibility(View.VISIBLE);
            fabOfferAllDeliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(deliver) > 0) {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_chat_receiver_id", ShopProfile.id);
                        bundle.putString("shop_chat_receiver_name", ShopProfile.title);
                        ChatRoom offerProfile = new ChatRoom();


                        Bundle bundle1=getArguments();
                        if (bundle1!=null){

                            bundle1.putString("from",bundle1.getString("from"));
                            offerProfile.setArguments(bundle);

                            if (getArguments().getString("from","Default").equals("home")){
                                Log.e("TRANSACTION ","First");
                                ft.replace(R.id.fClientHomeLayout, offerProfile);
                            }else {
                                Log.e("TRANSACTION ","Second");
                                ft.replace(R.id.fClientShop, offerProfile);
                            }

                        }else {
                            Log.e("TRANSACTION ","Third");
                            bundle1.putString("from",bundle1.getString("from"));
                            offerProfile.setArguments(bundle);
                            ft.replace(R.id.fClientHomeLayout, offerProfile);
                        }
                        ft.commit();



                    } /*else {
                    Toast.makeText(getActivity(), "غير متاح", Toast.LENGTH_LONG).show();
                }*/
                }
            });



        }else {
            fabOfferAllDeliver.setVisibility(View.GONE);
        }



        fabOfferAllRequest = (Button) v.findViewById(R.id.fabOfferAllRequest);
        fabOfferAllRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar.make(v,"قريبا ! غير متاح حاليا بهذه المدينة",Snackbar.LENGTH_LONG).setAction("Action",null).show();

          /*      Bundle bundle = new Bundle();
                bundle.putString("all_offer_reservation_id", id);
                bundle.putString("all_offer_reservation_img", img);
                bundle.putString("all_offer_reservation_desc", desc);
                bundle.putString("all_offer_reservation_price", price);
                bundle.putString("all_offer_reservation_title", title);
                bundle.putString("all_offer_reservation_quantity", quantity);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                AllOfferReservation productReservation = new AllOfferReservation();
                Bundle bundle1=getArguments();
                if (bundle1!=null){

                    bundle1.putString("from",bundle1.getString("from"));
                    productReservation.setArguments(bundle);

                    if (getArguments().getString("from","Default").equals("home")){
                        Log.e("TRANSACTION ","First");
                        ft.replace(R.id.fClientHomeLayout, productReservation);
                    }else {
                        Log.e("TRANSACTION ","Second");
                        ft.replace(R.id.fClientShop, productReservation);
                    }

                }else {
                    Log.e("TRANSACTION ","Third");
                    bundle1.putString("from",bundle1.getString("from"));
                    productReservation.setArguments(bundle);
                    ft.replace(R.id.fClientHomeLayout, productReservation);
                }
                ft.commit();*/
            }
        });

        //Set Up Share the Shop
        setUpShareTheShop();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (reservation.equals("0")) {
            fabSingleProduct.setVisibility(View.GONE);
        } else {
            fabSingleProduct.setVisibility(View.VISIBLE);
            fabSingleProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(sessionManager.isLoggedIn())) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                getActivity());
                        builderSingle.setTitle("Alert");
                        builderSingle.setIcon(R.drawable.logo_home);
                        builderSingle.setMessage("You Cannot Reserve This Product Without Register! " + "\n" +
                                "Please Register Now.");
                        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderSingle.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getActivity(), Login.class);
                                startActivity(intent);
                            }
                        });
                        builderSingle.show();
                    } else {
                        Bundle bundle = new Bundle();
                        if (AllSingleOffer.bundle.containsKey("shop_id")){
                            bundle.putString("shop_id",AllSingleOffer.bundle.getString("shop_id"));
                            Log.e("SHOP ID ALL",bundle.getString("shop_id"));
                        }
                        bundle.putString("all_offer_reservation_id", id);
                        bundle.putString("all_offer_reservation_img", img);
                        bundle.putString("all_offer_reservation_desc", desc);
                        bundle.putString("all_offer_reservation_price", price);
                        bundle.putString("all_offer_reservation_title", title);
                        bundle.putString("all_offer_reservation_quantity", quantity);
                        bundle.putString("all_offer_reservation_per",per);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        AllOfferReservation productReservation = new AllOfferReservation();

                        Bundle bundle1=getArguments();
                        if (bundle1!=null){

                            bundle1.putString("from",bundle1.getString("from"));
                            productReservation.setArguments(bundle);

                            if (getArguments().getString("from","Default").equals("home")){
                                Log.e("TRANSACTION ","First");
                                ft.replace(R.id.fClientHomeLayout, productReservation);
                            }else {
                                Log.e("TRANSACTION ","Second");
                                ft.replace(R.id.fClientShop, productReservation);
                            }

                        }else {
                            Log.e("TRANSACTION ","Third");
                            bundle1.putString("from",bundle1.getString("from"));
                            productReservation.setArguments(bundle);
                            ft.replace(R.id.fClientHomeLayout, productReservation);
                        }
                        ft.commit();
                        flag=0;
                    }
                }
            });
        }

    }

    private void setUpShareTheShop() {
        FacebookSdk.sdkInitialize(getActivity());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        bottomSheetLayout =(BottomSheetLayout) v.findViewById(R.id.client_home_bottomsheet);
        shareFabBtn= v.findViewById(R.id.shopProfileShare);

        shareFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgSingleProduct.setDrawingCacheEnabled(true);
                imgSingleProduct.buildDrawingCache();
                sharedBitmap = imgSingleProduct.getDrawingCache();

                handleShareLink();

                imgSingleProduct.setDrawingCacheEnabled(false); // clear drawing cache
            }
        });

    }

    private void handleShareLink() {

        //Share
        //Share Intent for Text Only
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.dasta.eemapp");
        shareIntent.setType("text/plain");

        //Share Intent for Text and Image
        if (Utility.checkPermissionREAD_EXTERNAL_STORAGE(getActivity())&&Utility.checkPermissionWRITE_EXTERNAL_STORAGE(getActivity())){


            final Intent imgShareIntent = new Intent(Intent.ACTION_SEND);
            imgShareIntent.setType("image/*");
            imgShareIntent.putExtra(Intent.EXTRA_STREAM,Utility.getImageUri(getActivity(), sharedBitmap));
            imgShareIntent.putExtra(Intent.EXTRA_TEXT,title);


            IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(getActivity(), imgShareIntent, "مشاركة...",
                    new IntentPickerSheetView.OnIntentPickedListener() {
                        @Override
                        public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo) {
                            bottomSheetLayout.dismissSheet();
                            if (activityInfo.componentName.getPackageName().equals("com.twitter.android")){

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

}
