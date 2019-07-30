package com.dasta.eemapp.fragment.Client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dasta.eemapp.R;

public class UserProfile extends Fragment {

    LinearLayout llClientProfileEdit, llClientProfileOrder, llClientProfileChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_profile, null);

        //**** Edit Profile
        llClientProfileEdit = (LinearLayout) v.findViewById(R.id.llClientProfileEdit);
        llClientProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                EditProfileData editProfileData = new EditProfileData();
                ft.replace(R.id.fClientProfile, editProfileData);
                ft.commit();
            }
        });

        //**** Order Profile
        llClientProfileOrder = (LinearLayout) v.findViewById(R.id.llClientProfileOrder);
        llClientProfileOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ClientOrder clientOrder = new ClientOrder();
                ft.replace(R.id.fClientProfile, clientOrder);
                ft.commit();
            }
        });

        llClientProfileChat = (LinearLayout) v.findViewById(R.id.llClientProfileChat);
        llClientProfileChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ClientChat clientChat = new ClientChat();
                ft.replace(R.id.fClientProfile, clientChat);
                ft.commit();
            }
        });

        return v;
    }
}
