package com.dasta.eemapp.fragment.Client;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.dasta.eemapp.activity.Client.Login;
import com.dasta.eemapp.helper.Client.SessionManager;
import com.squareup.picasso.Picasso;

public class SingleOffer extends Fragment {

    ImageView imgSingleProduct;
    TextView txtSingleProduct,descTxt,beforeTxt,percentTxt,afterTxt;
    Button fabSingleProduct;
    public static String id, img, price, title, desc, per, quantity;
    public static int flag = 0;
    int saleprice, price1, per1;
    SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_single_offer, null);
        flag = 1;
        OfferProfile.flag = 0;

        sessionManager = new SessionManager(getActivity());

        price = getArguments().getString("single_offer_price");
        title = getArguments().getString("single_offer_title");
        img = getArguments().getString("single_offer_img");
        desc = getArguments().getString("single_offer_desc");
        id = getArguments().getString("single_offer_id");
        per = getArguments().getString("single_offer_per");
        quantity = getArguments().getString("single_offer_quantity");

        price1 = Integer.parseInt(price);
        per1 = 100 - (Integer.parseInt(per));
        saleprice = (price1 * per1) / 100;

        imgSingleProduct = (ImageView) v.findViewById(R.id.imgSingleOffer);
//        Picasso.with(getActivity()).load(img).error(R.drawable.logo_home)
//                .placeholder(R.drawable.logo_home).into(imgSingleProduct);
        Glide.with(getActivity()).load(img)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgSingleProduct);

        txtSingleProduct = (TextView) v.findViewById(R.id.txtSingleOffer);
        descTxt=(TextView) v.findViewById(R.id.txtSingleOfferDesc);
        beforeTxt=(TextView) v.findViewById(R.id.txtSingleOfferpriceBefor);
        afterTxt=(TextView) v.findViewById(R.id.txtSingleOfferPirceAfter);
        percentTxt=(TextView) v.findViewById(R.id.txtSingleOfferpercent);
        txtSingleProduct.setText(title);
        descTxt.setText(desc);
        beforeTxt.setText("EGP ".concat(price));
        afterTxt.setText("EGP ".concat(String.valueOf(saleprice)));
        percentTxt.setText("% ".concat(per));

        fabSingleProduct = (Button) v.findViewById(R.id.fabOfferReservation);
        fabSingleProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(sessionManager.isLoggedIn())) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                            getActivity());
                    builderSingle.setTitle("تنبيه");
                    builderSingle.setIcon(R.drawable.logo_home);
                    builderSingle.setMessage("لا تسطيع حجز هذا المنتج قم بالتسجيل اولا !" + "\n" +
                            "من فضلك سجل الان");
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
                    builderSingle.show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("offer_reservation_id", id);
                    bundle.putString("offer_reservation_img", img);
                    bundle.putString("offer_reservation_desc", desc);
                    bundle.putString("offer_reservation_price", price);
                    bundle.putString("offer_reservation_title", title);
                    bundle.putString("offer_reservation_quantity", quantity);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    OfferReservation productReservation = new OfferReservation();
                    productReservation.setArguments(bundle);
                    ft.replace(R.id.fClientShop, productReservation);
                    ft.commit();
                }
            }
        });


        return v;
    }
}
