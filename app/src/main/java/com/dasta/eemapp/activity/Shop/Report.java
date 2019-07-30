package com.dasta.eemapp.activity.Shop;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Shop.AddCategory;
import com.dasta.eemapp.fragment.Shop.AddDepartment;
import com.dasta.eemapp.fragment.Shop.AddOffer;
import com.dasta.eemapp.fragment.Shop.Appointment;
import com.dasta.eemapp.fragment.Shop.Category;
import com.dasta.eemapp.fragment.Shop.Department;
import com.dasta.eemapp.fragment.Shop.EditDepartment;
import com.dasta.eemapp.fragment.Shop.LiveInfo;
import com.dasta.eemapp.fragment.Shop.LivePerview;
import com.dasta.eemapp.fragment.Shop.Offer;
import com.dasta.eemapp.fragment.Shop.Product;
import com.dasta.eemapp.fragment.Shop.ReportDeptSales;
import com.dasta.eemapp.fragment.Shop.ReportOffersSales;
import com.dasta.eemapp.fragment.Shop.ReportProductSales;
import com.dasta.eemapp.fragment.Shop.ReportReservation;

public class Report extends AppCompatActivity {


    private ImageView imgReportDept, imgReportOffers, imgReportReservation, imgReportProduct, imgShopReportHome;
    private TextView txtReportTitle;
    public static int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_report);

        flag = 1;
        Category.flag = 0;
        LiveInfo.flag = 0;
        Offer.flag = 0;
        Appointment.flag = 0;
        AddOffer.key = 0;
        AddOffer.flag = 0;
        AddCategory.flag1 = 0;
        AddDepartment.flag = 0;
        EditProduct.flag = 0;
        EditDepartment.flag = 0;
        Department.flag = 0;
        com.dasta.eemapp.fragment.Shop.Home.flag = 0;
        Product.flag = 0;

        txtReportTitle = (TextView) findViewById(R.id.txtShopReportTitle);
        imgReportDept = (ImageView) findViewById(R.id.imgShopReportDept);
        imgReportOffers = (ImageView) findViewById(R.id.imgShopReportOffers);
        imgReportReservation = (ImageView) findViewById(R.id.imgShopReportReservation);
        imgReportProduct = (ImageView) findViewById(R.id.imgShopReportProduct);
        imgShopReportHome = (ImageView) findViewById(R.id.imgShopReportHome);

        imgShopReportHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportProductSales.flag = 0;
                ReportDeptSales.flag = 0;
                ReportReservation.flag = 0;
                ReportOffersSales.flag = 0;

                Intent intent = new Intent(Report.this, Home.class);
                startActivity(intent);
                finish();
            }
        });


        txtReportTitle.setText(R.string.reportproduct);
        imgReportProduct.setColorFilter(Color.rgb(205, 32, 40));
        imgReportOffers.setColorFilter(Color.WHITE);
        imgReportReservation.setColorFilter(Color.WHITE);
        imgReportDept.setColorFilter(Color.WHITE);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ReportProductSales reportProductSales = new ReportProductSales();
        ft.add(R.id.fShopReport, reportProductSales);
        ft.commit();

        imgReportProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtReportTitle.setText(R.string.reportproduct);
                imgReportProduct.setColorFilter(Color.rgb(205, 32, 40));
                imgReportOffers.setColorFilter(Color.WHITE);
                imgReportReservation.setColorFilter(Color.WHITE);
                imgReportDept.setColorFilter(Color.WHITE);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ReportProductSales reportProductSales1 = new ReportProductSales();
                ft.replace(R.id.fShopReport, reportProductSales1);
                ft.commit();
            }
        });

        imgReportDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtReportTitle.setText(R.string.reportdept);
                imgReportProduct.setColorFilter(Color.WHITE);
                imgReportOffers.setColorFilter(Color.WHITE);
                imgReportReservation.setColorFilter(Color.WHITE);
                imgReportDept.setColorFilter(Color.rgb(205, 32, 40));

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ReportDeptSales reportDeptSales = new ReportDeptSales();
                ft.replace(R.id.fShopReport, reportDeptSales);
                ft.commit();
            }
        });

        imgReportReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtReportTitle.setText(R.string.reportreservation);
                imgReportProduct.setColorFilter(Color.WHITE);
                imgReportOffers.setColorFilter(Color.WHITE);
                imgReportReservation.setColorFilter(Color.rgb(205, 32, 40));
                imgReportDept.setColorFilter(Color.WHITE);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ReportReservation reportReservation = new ReportReservation();
                ft.replace(R.id.fShopReport, reportReservation);
                ft.commit();
            }
        });

        imgReportOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtReportTitle.setText(R.string.reportoffers);
                imgReportProduct.setColorFilter(Color.WHITE);
                imgReportOffers.setColorFilter(Color.rgb(205, 32, 40));
                imgReportReservation.setColorFilter(Color.WHITE);
                imgReportDept.setColorFilter(Color.WHITE);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ReportOffersSales reportOffersSales = new ReportOffersSales();
                ft.replace(R.id.fShopReport, reportOffersSales);
                ft.commit();
            }
        });

    }


    @Override
    public void onBackPressed() {

        if (ReportDeptSales.flag == 1) {

            ReportDeptSales.flag = 0;
            txtReportTitle.setText(R.string.reportproduct);
            imgReportProduct.setColorFilter(Color.rgb(205, 32, 40));
            imgReportOffers.setColorFilter(Color.WHITE);
            imgReportReservation.setColorFilter(Color.WHITE);
            imgReportDept.setColorFilter(Color.WHITE);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ReportProductSales reportProductSales = new ReportProductSales();
            ft.replace(R.id.fShopReport, reportProductSales);
            ft.commit();

        } else if (ReportOffersSales.flag == 1) {

            ReportOffersSales.flag = 0;
            txtReportTitle.setText(R.string.reportproduct);
            imgReportProduct.setColorFilter(Color.rgb(205, 32, 40));
            imgReportOffers.setColorFilter(Color.WHITE);
            imgReportReservation.setColorFilter(Color.WHITE);
            imgReportDept.setColorFilter(Color.WHITE);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ReportProductSales reportProductSales = new ReportProductSales();
            ft.replace(R.id.fShopReport, reportProductSales);
            ft.commit();

        } else if (ReportReservation.flag == 1) {

            ReportReservation.flag = 0;
            txtReportTitle.setText(R.string.reportproduct);
            imgReportProduct.setColorFilter(Color.rgb(205, 32, 40));
            imgReportOffers.setColorFilter(Color.WHITE);
            imgReportReservation.setColorFilter(Color.WHITE);
            imgReportDept.setColorFilter(Color.WHITE);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ReportProductSales reportProductSales = new ReportProductSales();
            ft.replace(R.id.fShopReport, reportProductSales);
            ft.commit();

        } else if (ReportProductSales.flag == 1) {

            ReportProductSales.flag = 0;
            Intent intent = new Intent(Report.this, Home.class);
            startActivity(intent);

        } else {
            finish();
            super.onBackPressed();
        }
    }
}
