package com.dasta.eemapp.fragment.Client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Login;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.helper.Client.SessionManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;
import com.facebook.share.model.ShareLinkContent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;

public class SingleProduct extends Fragment {

    View v;
    ImageView imgSingleProduct;
    TextView txtSingleProduct,desctxt,priceTxt;
    Button fabSingleProduct, fabSingleProductRequest, fabSingleProductDeliver;
    public static String id, img, price, title, desc, quantity, reservation, deliver;
    public static int flag = 0;
    SessionManager sessionManager;
    RequestQueue requestQueue;

    //Share Views
    FloatingActionButton shareFabBtn;
    BottomSheetLayout bottomSheetLayout;
    Bitmap sharedBitmap;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         v = inflater.inflate(R.layout.fragment_client_single_product, null);

        flag = 1;
        ProductProfile.flag = 0;

        sessionManager = new SessionManager(getActivity());

        price = getArguments().getString("single_product_price");
        title = getArguments().getString("single_product_title");
        img = getArguments().getString("single_product_img");
        desc = getArguments().getString("single_product_desc");
        id = getArguments().getString("single_product_id");
        quantity = getArguments().getString("single_product_quantity");
        reservation = getArguments().getString("single_product_reservation");
        deliver = getArguments().getString("single_product_deliver");


        imgSingleProduct = (ImageView) v.findViewById(R.id.imgSingleProduct);
//        Picasso.with(getActivity()).load(img).error(R.drawable.logo_home)
//                .placeholder(R.drawable.logo_home).into(imgSingleProduct);
        Glide.with(getActivity()).load(img)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgSingleProduct);

        txtSingleProduct = (TextView) v.findViewById(R.id.txtSingleProduct);
        desctxt= (TextView) v.findViewById(R.id.txtSingleProductdesc);
        priceTxt= (TextView) v.findViewById(R.id.txtSingleProductprice);

        txtSingleProduct.setText(title );
        desctxt.setText(desc);
        priceTxt.setText(price.concat(" EGP"));

        fabSingleProduct = (Button) v.findViewById(R.id.fabProductReservation);
        if (reservation.equals("0")) {
            fabSingleProduct.setVisibility(View.GONE);
        } else {
            fabSingleProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!(sessionManager.isLoggedIn())) {

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
                                dialog.dismiss();
                                startActivity(intent);
                            }
                        });

                        dialog.show();


                        /*AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                getActivity());
                        builderSingle.setTitle("تنبيه");
                        builderSingle.setIcon(R.drawable.logo_home);
                        builderSingle.setMessage("لا تسطيع حجز هذا المنتج الان !" + "\n" +
                                "من فضلك قم بالتسجيل اولا");
                        builderSingle.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderSingle.setPositiveButton("تسجيل", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getActivity(), Login.class);
                                startActivity(intent);
                            }
                        });
                        builderSingle.show();*/
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("product_reservation_id", id);
                        bundle.putString("product_reservation_img", img);
                        bundle.putString("product_reservation_desc", desc);
                        bundle.putString("product_reservation_price", price);
                        bundle.putString("product_reservation_title", title);
                        bundle.putString("product_reservation_quantity", quantity);
                        bundle.putString("product_reservation", reservation);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ProductReservation productReservation = new ProductReservation();
                        productReservation.setArguments(bundle);
                        ft.replace(R.id.fClientShop, productReservation);
                        ft.commit();
                        SingleProduct.flag=0;
                    }
                }
            });
        }
        fabSingleProductDeliver = (Button) v.findViewById(R.id.fabSingleProductDeliver);
        if (Integer.parseInt(deliver) > 0) {
            fabSingleProductDeliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(deliver) > 0) {

                        if (!(sessionManager.isLoggedIn())) {

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
                                    dialog.dismiss();
                                    startActivity(intent);
                                }
                            });

                            dialog.show();


                        /*AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                getActivity());
                        builderSingle.setTitle("تنبيه");
                        builderSingle.setIcon(R.drawable.logo_home);
                        builderSingle.setMessage("لا تسطيع حجز هذا المنتج الان !" + "\n" +
                                "من فضلك قم بالتسجيل اولا");
                        builderSingle.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderSingle.setPositiveButton("تسجيل", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getActivity(), Login.class);
                                startActivity(intent);
                            }
                        });
                        builderSingle.show();*/
                        } else {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putString("shop_chat_receiver_id", ShopProfile.id);
                            bundle.putString("shop_chat_receiver_name", ShopProfile.title);
                            ChatRoom offerProfile = new ChatRoom();
                            offerProfile.setArguments(bundle);
                            ft.replace(R.id.fClientShop, offerProfile);
                            ft.commit();
                        }


                    }
                }
            });
        }else {
            fabSingleProductDeliver.setVisibility(View.GONE);
        }


        fabSingleProductRequest = (Button) v.findViewById(R.id.fabSingleProductRequest);
        fabSingleProductRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar.make(v,"قريبا ! غير متاح حاليا بهذه المدينة",Snackbar.LENGTH_LONG).setAction("Action",null).show();

             /*   if (!(sessionManager.isLoggedIn())) {

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
                            dialog.dismiss();
                            startActivity(intent);
                        }
                    });

                    dialog.show();


                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("product_reservation_id", id);
                    bundle.putString("product_reservation_img", img);
                    bundle.putString("product_reservation_desc", desc);
                    bundle.putString("product_reservation_price", price);
                    bundle.putString("product_reservation_title", title);
                    bundle.putString("product_reservation_quantity", quantity);
                    bundle.putString("product_reservation", reservation);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ProductReservation productReservation = new ProductReservation();
                    productReservation.setArguments(bundle);
                    ft.replace(R.id.fClientShop, productReservation);
                    ft.commit();
                    SingleProduct.flag=0;
                }
*/

            }
        });

        //Set Up Share the Shop
        setUpShareTheShop();

        return v;
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
                imgSingleProduct.setDrawingCacheEnabled(true);
                imgSingleProduct.buildDrawingCache();
                sharedBitmap= imgSingleProduct.getDrawingCache();

                handleShareLink();

                imgSingleProduct.setDrawingCacheEnabled(false); // clear drawing cache
            }
        });

    }

    private void handleShareLink() {

        //Share Intent for Text Only
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"http://dasta.net/data/eem/Client/redirect_single_product.php/"
        .concat(price).concat("/").concat(desc).concat("/").concat(title).concat("/").concat(img).concat("/").concat(id)
        .concat("/").concat(quantity).concat("/").concat(reservation).concat("/").concat(deliver));
        shareIntent.setType("text/plain");

        //Share Intent for Text and Image
        if (Utility.checkPermissionREAD_EXTERNAL_STORAGE(getActivity())&&Utility.checkPermissionWRITE_EXTERNAL_STORAGE(getActivity())){

            final Intent imgShareIntent = new Intent(Intent.ACTION_SEND);
            imgShareIntent.setType("image/*");
            imgShareIntent.putExtra(Intent.EXTRA_STREAM,Utility.getImageUri(getActivity(), sharedBitmap));

            String url="http://dasta.net/data/eem/Client/redirect_single_product.php/"
                    .concat(price).concat("/").concat(desc).concat("/").concat(title).concat("/").concat(img.substring(45)).concat("/").concat(id)
                    .concat("/").concat(quantity).concat("/").concat(reservation).concat("/").concat(deliver);
            URI uri = null;
            try {
                 uri = new URI(url.replace(" ", "%20"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            imgShareIntent.putExtra(Intent.EXTRA_TEXT,uri.toString());


            final URI finalUri = uri;
            IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(getActivity(), imgShareIntent, "مشاركة...",
                    new IntentPickerSheetView.OnIntentPickedListener() {
                        @Override
                        public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo) {
                            bottomSheetLayout.dismissSheet();
                           if (activityInfo.componentName.getPackageName().equals("com.facebook.orca")) {
                               final Intent imgShareIntent2 = new Intent(Intent.ACTION_SEND);
                                imgShareIntent2.setType("text/plain");
                                imgShareIntent2.putExtra(Intent.EXTRA_TEXT, finalUri.toString());
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
//                                imgShareIntent2.putExtra(Intent.EXTRA_TEXT, finalUri.toString());
//                                startActivity(activityInfo.getConcreteIntent(imgShareIntent2));

                                   ShareLinkContent content = new ShareLinkContent.Builder()
                                           .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.dasta.eemapp"))
                                           .setQuote(ShopProfile.title.concat(" - ").concat(ShopProfile.cat).concat(" - ").
                                                   concat(ShopProfile.city).concat(" ").concat(ShopProfile.address)
                                                   .concat(" - ").concat(ShopProfile.phone).concat(" - ").concat(title)
                                           .concat(" - ").concat(desc).concat(" - ").concat(desc).concat(" - ").concat(price)
                                           .concat(" EGP"))
                                           .build();
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

}
