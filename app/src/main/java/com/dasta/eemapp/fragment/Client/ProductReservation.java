package com.dasta.eemapp.fragment.Client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.dasta.eemapp.activity.Client.*;
import com.dasta.eemapp.activity.Client.Home;
import com.dasta.eemapp.activity.Client.UserProfile;
import com.dasta.eemapp.activity.Shop.*;
import com.dasta.eemapp.helper.AppConfig;

import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;




public class ProductReservation extends Fragment {

    public static int flag = 0;
    int flag1 = 0;
    String product_id, product_img, product_desc, product_price, product_title, product_quantity, reservation;
    String shop_id, shop_logo, shop_address, shop_phone;
    String addData, quantity, userid;

    Button btnProductReservation, btnProductShipping;
    CircleImageView imgProductReservationLogo, imgProductReservationPhone;
    ImageView imgProductReservationImage;
    EditText etProductReservation;
    RequestQueue requestQueue;
    Spinner spProductReservationQuantity;
    List<String> list = new ArrayList<>();
    int q;
    private SQLiteHandler db;
    TextView pTitle,pDesc,pLoc,pPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_product_reservation, null);

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
        shop_id = ShopProfile.id;
        shop_logo = ShopProfile.logoUrl;
        shop_address = ShopProfile.address;
        shop_phone = ShopProfile.phone;
        product_id = getArguments().getString("product_reservation_id");
        product_img = getArguments().getString("product_reservation_img");
        product_desc = getArguments().getString("product_reservation_desc");
        product_price = getArguments().getString("product_reservation_price");
        product_title = getArguments().getString("product_reservation_title");
        product_quantity = getArguments().getString("product_reservation_quantity");
        reservation = getArguments().getString("product_reservation");

        /****/

        pTitle.setText(product_title);
        pDesc.setText(product_desc);
        pPrice.setText(product_price.concat(" EGP"));
        pLoc.setText(shop_address);
        /******/
        spProductReservationQuantity = (Spinner) v.findViewById(R.id.spProductReservationQuantity);
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
        etProductReservation = (EditText) v.findViewById(R.id.etClientProductReservation);

        /*****/
        imgProductReservationImage = (ImageView) v.findViewById(R.id.imgProductReservationImage);
//        Picasso.with(getActivity()).load(product_img)
//                .error(R.drawable.logo_home).placeholder(R.drawable.logo_home).into(imgProductReservationImage);
        Glide.with(getActivity()).load(product_img)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgProductReservationImage);

        /****/
        imgProductReservationLogo = (CircleImageView) v.findViewById(R.id.imgProductReservationLogo);
//        Picasso.with(getActivity()).load(shop_logo)
//                .error(R.drawable.logo_home).placeholder(R.drawable.logo_home).into(imgProductReservationLogo);
        Glide.with(getActivity()).load(shop_logo)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgProductReservationLogo);

        /*****/
        imgProductReservationPhone = (CircleImageView) v.findViewById(R.id.imgProductReservationPhone);
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

        btnProductReservation = (Button) v.findViewById(R.id.btnProductReservation);
        if(reservation.equals("0")){
            btnProductReservation.setEnabled(false);
        }else {
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
                    builderSingle.setMessage("هل تريد حجز هذا الصنف؟");
                    builderSingle.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderSingle.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addReservation(getActivity(), shop_id, product_id, addData, quantity, userid);
                        }
                    });
                    builderSingle.show();
                }
            });
        }
        btnProductShipping = v.findViewById(R.id.btnProductShip);
        btnProductShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setMessage("هل تريد حجز هذا الصنف؟");
                builderSingle.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderSingle.setPositiveButton("الموافقة", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addShipping(getActivity(), shop_id, product_id, addData, quantity, userid);
                    }
                });
                builderSingle.show();*/

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
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "حدث خطأ ما ..", Toast.LENGTH_LONG).show();
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
