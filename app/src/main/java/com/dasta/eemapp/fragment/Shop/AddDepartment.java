package com.dasta.eemapp.fragment.Shop;

import android.content.Context;
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

public class AddDepartment extends Fragment {

    private EditText etShopDepartmentName;
    private Button btnShopAddDepartment;
    String departmentname;
    WebServies webServies = new WebServies();

    public static String cat;
    public static int flag = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_add_department, null, false);

        com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.addDepartment);

        Offer.flag = 0;
        LiveInfo.flag = 0;
        Category.flag = 0;
        Appointment.flag = 0;
        Home.flag = 0;

        flag = 1;

        final String id = com.dasta.eemapp.activity.Shop.Home.shop_id;
        cat = getArguments().getString("shop_catt_specific");

        etShopDepartmentName = (EditText) v.findViewById(R.id.etShopDepartmentName);

        btnShopAddDepartment = (Button) v.findViewById(R.id.btnShopAddDepartment);
        btnShopAddDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    departmentname = etShopDepartmentName.getText().toString().trim();
                    webServies.addShopDepartment(getActivity(), id, departmentname, cat);
                }else{
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
