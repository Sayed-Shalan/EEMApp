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

public class EditDepartment extends Fragment {

    private EditText etShopDepartmentName;
    private Button btnShopEditDepartment;
    WebServies webServies = new WebServies();
    public static String id, id_edit, cat, dept_be, dept_af;

    public static int flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_edit_department, null, false);

        Offer.flag = 0;
        LiveInfo.flag = 0;
        Category.flag = 0;
        Appointment.flag = 0;
        Home.flag = 0;
        flag = 1;

        id = com.dasta.eemapp.activity.Shop.Home.shop_id;
        id_edit = getArguments().getString("shop_department_id_edit");
        cat = getArguments().getString("shop_department_cat_edit");
        dept_be = getArguments().getString("shop_department_be_edit");

        etShopDepartmentName = (EditText) v.findViewById(R.id.etShopEditDepartmentName);
        etShopDepartmentName.setText(dept_be);

        btnShopEditDepartment = (Button) v.findViewById(R.id.btnShopEditDepartment);
        btnShopEditDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_af = etShopDepartmentName.getText().toString().trim();
                if (isNetworkAvailable()) {
                    //Toast.makeText(getActivity(), dept_af + "\n" + id + "\n" + id_edit, Toast.LENGTH_LONG).show();
                    webServies.editShopDepartment(getActivity(), id, id_edit, dept_be, dept_af, cat);
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
