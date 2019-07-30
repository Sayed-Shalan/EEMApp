package com.dasta.eemapp.activity.Shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.WebServies;

/**
 * Created by Mohamed on 14/10/2017.
 */

public class ChangePassword extends FragmentActivity {

    EditText etShopNewPassword, etShopConfirmNewPassword;
    Button btnShopNewPassword;
    String newpass, confirmpass;
    WebServies webServies = new WebServies();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_change_password);

        etShopNewPassword = (EditText) findViewById(R.id.etShopNewPassword);

        etShopConfirmNewPassword = (EditText) findViewById(R.id.etShopConfirmNewPassword);

        btnShopNewPassword = (Button) findViewById(R.id.btnShopChangePassword);
        btnShopNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newpass = etShopNewPassword.getText().toString();
                confirmpass = etShopConfirmNewPassword.getText().toString();

                if (newpass.equals(confirmpass)) {
                    webServies.shopChangePassword(ChangePassword.this, Home.shop_id, newpass);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.confirmpass, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChangePassword.this, Home.class);
        startActivity(intent);
        finish();
    }
}
