package com.dasta.eemapp.fragment.Services;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dasta.eemapp.R;

/**
 * Created by Mohamed on 28/06/2017.
 */

public class Data extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_services_data, null);
        return v;
    }
}
