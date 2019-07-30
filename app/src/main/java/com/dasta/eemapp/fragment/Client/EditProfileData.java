package com.dasta.eemapp.fragment.Client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.*;
import com.dasta.eemapp.activity.Client.UserProfile;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.Client.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 25/09/2017.
 */

public class EditProfileData extends Fragment {

    EditText etEditRUsername, etEditRUserPhone, etEditRUserEmail;
    Button btnEditRUserSignup;
    String username, phone, email;
    RequestQueue requestQueue;
    public static int flag = 0;
    SQLiteHandler sqLiteHandler;
    private SQLiteHandler db;
    private SessionManager session;
    public static String user_name, user_id, user_phone, user_mail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_edit_profile, null);
        // SqLite database handler
        db = new SQLiteHandler(getActivity());

        // session manager
        session = new SessionManager(getActivity());

        flag = 1;

        sqLiteHandler = new SQLiteHandler(getActivity());

        etEditRUsername = (EditText) v.findViewById(R.id.etEditRUserName);
        etEditRUsername.setText(UserProfile.user_name);

        etEditRUserEmail = (EditText) v.findViewById(R.id.etEditRUserEmail);
        etEditRUserEmail.setText(UserProfile.user_mail);

        etEditRUserPhone = (EditText) v.findViewById(R.id.etEditRUserPhone);
        etEditRUserPhone.setText(UserProfile.user_phone);

        btnEditRUserSignup = (Button) v.findViewById(R.id.btnEditRUserSignup);
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        user_id = user.get("userid");
        user_name = user.get("username");
        user_phone = user.get("userphone");
        user_mail = user.get("usermail");

        etEditRUserEmail.setText(user_mail);
        etEditRUsername.setText(user_name);
        etEditRUserPhone.setText(user_phone);

        btnEditRUserSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = etEditRUsername.getText().toString().trim();
                phone = etEditRUserPhone.getText().toString().trim();
                email = etEditRUserEmail.getText().toString().trim();

                if (username.equals("") || phone.equals("") || email.equals("")) {
                    Toast.makeText(getActivity(), R.string.emptydata, Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> user = db.getUserDetails();

                    String user_id = user.get("userid");
                    editUser(Home.context, user_id, username, email, phone);
                }

            }
        });

        return v;
    }

    // Register Shop Owner
    public void editUser(final Activity activity, final String use_id,final String username, final String email,
                         final String phone) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);
        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response == "user is exists" || response == "Update Failed"
                                || response == "failed query" || response == "please add empty data"
                                || response == "error") {
                            Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        } else {
                            flag = 0;
                            sqLiteHandler.updateUser(use_id, username, email, phone);
                            Toast.makeText(activity,"تم تعديل البيانات بنجاح",Toast.LENGTH_SHORT).show();
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            Home_Category client_home_category = new Home_Category();
                            ft.add(R.id.fClientHomeLayout, client_home_category);
                            ft.commit();
//                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                            com.dasta.eemapp.fragment.Client.UserProfile userProfile = new com.dasta.eemapp.fragment.Client.UserProfile();
//                            ft.replace(R.id.fClientProfile, userProfile);
//                            ft.commit();
                        }
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
                params.put("userid", use_id);
                params.put("username", username);
                params.put("email", email);
                params.put("phone", phone);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

}
