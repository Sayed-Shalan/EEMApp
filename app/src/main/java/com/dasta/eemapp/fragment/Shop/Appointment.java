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
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Shop.Home;
import com.dasta.eemapp.helper.WebServies;

/**
 * Created by Mohamed on 28/06/2017.
 */

public class Appointment extends Fragment {

    private CalendarView cldShopAppointment;
    private EditText etShopAppointment;
    private Button btnShopAppointment;
    int appoDay, appoMonth, appoYear;
    WebServies webServies = new WebServies();
    String id, date, note;
    public static int flag = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shop_appointment, null, false);
        flag = 1;
        id = Home.shop_id;
        setRetainInstance(true);
        cldShopAppointment = (CalendarView) v.findViewById(R.id.cldShopAppointment);
        cldShopAppointment.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                appoDay = dayOfMonth;
                appoMonth = month + 1;
                appoYear = year;
            }
        });

        etShopAppointment = (EditText) v.findViewById(R.id.etShopAppointment);

        btnShopAppointment = (Button) v.findViewById(R.id.btnAddShopAppointment);
        btnShopAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = appoDay + "/" + appoMonth + "/" + appoYear;
                note = etShopAppointment.getText().toString().trim();
                if (isNetworkAvailable()) {
                    if (appoDay == 0) {
                        Toast.makeText(getActivity(), R.string.emptydata, Toast.LENGTH_LONG).show();
                    } else {
                        if (!(note.isEmpty())) {
                            webServies.addShopAppointment(getActivity(), date, note, id);
                            Toast.makeText(getActivity(), note + date, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.emptydata, Toast.LENGTH_LONG).show();
                        }
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
