package com.dasta.eemapp.fragment.Shop;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.WebServies;

/**
 * Created by Mohamed on 16/07/2017.
 */

public class AddOffer extends Fragment {

    private EditText etOfferSalePrice, etOfferSellPrice;
    private Button btnShopAddOffer;
    Double sell = 0.0, sale = 0.0, per = 0.0;
    int per1 = 0;
    String sellprice, saleprice, id;
    WebServies webServies = new WebServies();
    public static int flag = 0;
    public static int key = 0;
    public static String dept, cat;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_add_offer, null, false);

        LiveInfo.flag = 0;
        Category.flag = 0;
        Appointment.flag = 0;
        Home.flag = 0;

        Offer.flag = 0;
        flag = 1;


        cat = getArguments().getString("offer_cat");
        dept = getArguments().getString("offer_dept");

        id = getArguments().getString("offer_id");

        etOfferSellPrice = (EditText) v.findViewById(R.id.etOfferSellPrice);
        etOfferSellPrice.setText(getArguments().getString("offer_sell_price"));

        etOfferSalePrice = (EditText) v.findViewById(R.id.etOfferSalePrice);
        etOfferSalePrice.setText(getArguments().getString("offer_sale_price"));

        btnShopAddOffer = (Button) v.findViewById(R.id.btnShopAddOffer);
        btnShopAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellprice = etOfferSellPrice.getText().toString().trim();
                saleprice = etOfferSalePrice.getText().toString().trim();

                if (isNetworkAvailable()) {
                    if (!(sellprice.isEmpty()) && !(saleprice.isEmpty())) {
                        sell = Double.parseDouble(sellprice);
                        sale = Double.parseDouble(saleprice);

                        if (sell > sale) {
                            per = (((sell - sale)) / sell) * 100;
                            per1 = per.intValue();
                            Toast.makeText(getActivity(), sell + "\n" + sale + "\n" + per1, Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                    getActivity());
                            builderSingle.setTitle(getString(R.string.questionSale));
                            builderSingle.setMessage(getString(R.string.questionPercentage) + per1 + "%");
                            builderSingle.setNegativeButton(getString(R.string.questionNo), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builderSingle.setPositiveButton(getString(R.string.questionYes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    webServies.addShopOffer(getActivity(), saleprice, per1 + "", id);
                                }
                            });

                            builderSingle.show();
                        } else {
                            Toast.makeText(getActivity(), R.string.offerMessge, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.emptydata, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
