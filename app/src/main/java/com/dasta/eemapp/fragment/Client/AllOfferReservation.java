package com.dasta.eemapp.fragment.Client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllOfferReservation extends Fragment {

    public static int flag = 0;
    int flag1 = 0;
    String product_id, product_img, product_desc, product_price, product_title, product_quantity;
    String shop_id, shop_logo, shop_address, shop_phone,perl;
    String addData, quantity, userid;

    TextView pTitle,pDesc,pLoc,pPrice;
    Button btnProductReservation, btnProductShipping;
    CircleImageView imgProductReservationLogo, imgProductReservationPhone;
    ImageView imgProductReservationImage;
    EditText etProductReservation;
    RequestQueue requestQueue;
    Spinner spProductReservationQuantity;
    List<String> list = new ArrayList<>();
    int q;
    private SQLiteHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_all_offer_reservation, null);

        flag = 1;
        AllSingleOffer.flag = 0;

        requestQueue = Volley.newRequestQueue(getActivity());

        // SqLite database handler
        db = new SQLiteHandler(getActivity());
        pTitle=v.findViewById(R.id.txtProductReservationPDataTitle);
        pDesc=v.findViewById(R.id.txtProductReservationPDataDesc);
        pLoc=v.findViewById(R.id.txtProductReservationPDataLocation);
        pPrice=v.findViewById(R.id.txtProductReservationPDataPrice);


        // Fetching user details from sqlite
        final HashMap<String, String> user = db.getUserDetails();
        userid = user.get("userid");

        flag = 1;
        try {
            if (getArguments().containsKey("shop_id")){
                shop_id =getArguments().getString("shop_id");
                Log.e("SHOP ID in ","InAll :"+getArguments().getString("shop_id"));
            }else {
                shop_id = ShopProfile.id;
                Log.e("INSIDE ELSE","---");

            }
        }catch (NullPointerException e){
            shop_id = ShopProfile.id;
            Log.e("INSIDE CATCH","---");
        }

//        shop_id = ShopProfile.id;
        shop_logo = ShopProfile.logoUrl;
        shop_address = ShopProfile.address;
        shop_phone = ShopProfile.phone;
        product_id = getArguments().getString("all_offer_reservation_id");
        product_img = getArguments().getString("all_offer_reservation_img");
        product_desc = getArguments().getString("all_offer_reservation_desc");
        product_price = getArguments().getString("all_offer_reservation_price");
        product_title = getArguments().getString("all_offer_reservation_title");
        product_quantity = getArguments().getString("all_offer_reservation_quantity");
        perl=getArguments().getString("all_offer_reservation_per");


        /****/
        pTitle.setText(product_title);
        pDesc.setText(product_desc);
        pPrice.setText(product_price);
       // pLoc.setText(shop_address);

        /******/
        spProductReservationQuantity = (Spinner) v.findViewById(R.id.spAllOfferReservationQuantity);
        q = Integer.parseInt(product_quantity);
        for (int i = 1; i <= q; i++) {
            list.add(i + "");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnerview_item, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                return v;
            }
        };
        spProductReservationQuantity.setAdapter(arrayAdapter);
        spProductReservationQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag1 == 0) {
                    flag1 = 1;
                } else {
                    flag1 = 0;
                    quantity = spProductReservationQuantity.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /****/
        etProductReservation = (EditText) v.findViewById(R.id.etClientAllOfferReservation);

        /*****/
        imgProductReservationImage = (ImageView) v.findViewById(R.id.imgAllOfferReservationImage);
//        Picasso.with(getActivity()).load(product_img)
//                .error(R.drawable.logo_home).placeholder(R.drawable.logo_home).into(imgProductReservationImage);
        Glide.with(getActivity()).load(product_img)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgProductReservationImage);

        /****/
        imgProductReservationLogo =  v.findViewById(R.id.imgAllOfferReservationLogo);
//        Picasso.with(getActivity()).load(shop_logo)
//                .error(R.drawable.logo_home).placeholder(R.drawable.logo_home).into(imgProductReservationLogo);
        Glide.with(getActivity()).load(shop_logo)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgProductReservationLogo);

        /*****/
        imgProductReservationPhone =  v.findViewById(R.id.imgAllOfferReservationPhone);
        imgProductReservationPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.select_dialog_item);
                arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

                arrayAdapter.add(shop_phone);

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
                                startActivity(i);
                            }
                        });
                builderSingle.show();
            }
        });

        btnProductReservation = v.findViewById(R.id.btnAllOfferReservation);
        btnProductReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData = etProductReservation.getText().toString().trim();

                if (addData.equals(null)) {
                    addData = "";
                }

                if (quantity != null && !(quantity.isEmpty())) {

                } else {
                    quantity = "1";
                }

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setMessage("هل تريد حجز هذا المنتج؟");
                builderSingle.setNegativeButton("العاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderSingle.setPositiveButton("حجز", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addReservation(getActivity(), shop_id, product_id, addData, quantity, userid);
                    }
                });
                builderSingle.show();
            }
        });

        btnProductShipping =  v.findViewById(R.id.btnAllOfferShip);
        btnProductShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar.make(v,"قريبا ! غير متاح حاليا بهذه المدينة",Snackbar.LENGTH_LONG).setAction("Action",null).show();


/*

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setMessage("Are you sure that you want to reserve this product ?");
                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderSingle.setPositiveButton("Accept Shipping", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addShipping(getActivity(), shop_id, product_id, addData, quantity, userid);
                    }
                });
                builderSingle.show();
*/

            }
        });

        return v;
    }

    // Add Reservation
    public void addReservation(final Activity activity, final String shopid, final String productid,
                               final String adddata, final String quantity, final String userid) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_PRODUCT_RESERVATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, "تم حجز الصنف بنجاح..", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("shopid", shopid);
                params.put("productid", productid);
                params.put("adddata", adddata);
                params.put("quantity", quantity);
                return params;
            }
        };

        Log.e("DATA OF RESERVATION",userid.concat(" - ")
                .concat(shopid).concat(" - ").
                        concat(productid).concat(" - ").
                        concat(adddata)
        .concat(" - ").concat(quantity));

        //*** add request data from link into volley connection
        request.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Add Shipping
    public void addShipping(final Activity activity, final String shopid, final String productid,
                            final String adddata, final String quantity, final String userid) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_PRODUCT_SHIPPING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("shopid", shopid);
                params.put("productid", productid);
                params.put("adddata", adddata);
                params.put("quantity", quantity);
                return params;
            }
        };
        //*** add request data from link into volley connection
        request.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //*** add request data from link into volley connection
        requestQueue.add(request);

    }

}
