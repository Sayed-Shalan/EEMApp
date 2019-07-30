package com.dasta.eemapp.activity.Shop;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.adapter.Shop.DrawerListAdapter;
import com.dasta.eemapp.fragment.Client.ChatRoom;
import com.dasta.eemapp.fragment.Shop.AddCategory;
import com.dasta.eemapp.fragment.Shop.AddDepartment;
import com.dasta.eemapp.fragment.Shop.AddOffer;
import com.dasta.eemapp.fragment.Shop.AddOfferProduct;
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
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Shop.NavItem;
import com.dasta.eemapp.helper.Shop.SQLiteHandler;
import com.dasta.eemapp.helper.Shop.SessionManager;
import com.dasta.eemapp.helper.WebServies;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Home extends FragmentActivity {

    public static TextView txtShopHomeTitle;
    public ImageView imgShopLogout, imgShopDrawer, imgShopCategory, imgShopHome, imgShopImageEdit;
    public static String shop_id, shop_name, shop_phone, shop_address,
            owner_name, owner_mail, owner_username, city, mall, image, lat, lng, open;
    String op;
    private SQLiteHandler db;
    private SessionManager session;
    public static ImageView imgShopLogo;
    /// Drawer Data
    private ListView drawer_list;
    private RelativeLayout drawer_page;
    private DrawerLayout drawer_layout;
    public static String view;
    private TextView txtNavTitle, txtShopView;
    ArrayList<NavItem> items_drawer = new ArrayList<>();
    RequestQueue requestQueue;
    WebServies webServies = new WebServies();
    Bitmap sharedBitmap;


    CallbackManager callbackManager;
    ShareDialog shareDialog;


    BottomSheetLayout bottomSheetLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_home);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());


        //

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        shop_id = user.get("shopid");
        shop_name = user.get("shopname");
        shop_phone = user.get("shopphone");
        shop_address = user.get("shopaddress");
        owner_name = user.get("ownername");
        owner_mail = user.get("ownermail");
        owner_username = user.get("ownerusername");
        city = user.get("ownercity");
        mall = user.get("ownermall");
        //image = user.get("shopimage");
        image = "http://dasta.net/data/eem/Upload/Shop/Image/" + shop_id + ".png";
        lat = user.get("shoplat");
        lng = user.get("shoplng");
        open = user.get("shopopen");

        //**** Define Content of Home
        txtShopHomeTitle = (TextView) findViewById(R.id.txtShopHomeTitle);

        //**** VIEWS
        bottomSheetLayout=(BottomSheetLayout) findViewById(R.id.shop_bottom_sheet);
        txtShopView = (TextView) findViewById(R.id.txtShopView);
        shopView(shop_id);

        imgShopDrawer = (ImageView) findViewById(R.id.imgShopDrawer);
        //imgShopLogout = (ImageView) findViewById(R.id.imgShopLogout);
        imgShopCategory = (ImageView) findViewById(R.id.imgShopCategory);
        imgShopHome = (ImageView) findViewById(R.id.imgShopHome);
        imgShopImageEdit = (ImageView) findViewById(R.id.imgShopImageEdit);

        imgShopImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, EditShopImage.class);
                intent.putExtra("edit_shop_image_id", shop_id);
                startActivity(intent);
            }
        });

        imgShopHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category.flag = 0;
                LivePerview.flag = 0;
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
                Report.flag = 0;
                ReportOffersSales.flag = 0;
                ReportDeptSales.flag = 0;
                ReportReservation.flag = 0;
                ReportProductSales.flag = 0;
                //***
                //*** Default Title
                txtShopHomeTitle.setText(R.string.live);
                //*** Default ColorFilter
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
                ft.replace(R.id.fShopHomeLayout, home);
                ft.commit();
            }
        });

        imgShopCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, com.dasta.eemapp.activity.Category.class);
                startActivity(intent);
                finish();
            }
        });

        txtShopHomeTitle.setText(R.string.live);
        //*** Default ColorFilter
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
        ft.replace(R.id.fShopHomeLayout, home);
        ft.commit();

        // drawer layout
        DrawerListAdapter adapter = new DrawerListAdapter(this, items_drawer);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer_page = (RelativeLayout) findViewById(R.id.drawerLeft);
        drawer_list = (ListView) findViewById(R.id.navListLeft);
        drawer_list.setAdapter(adapter);

        drawer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        txtNavTitle = (TextView) findViewById(R.id.txtNavTitle);
        txtNavTitle.setText(shop_name + "\n" + shop_address);

        imgShopLogo = (ImageView) findViewById(R.id.imgShopLogo);
        Picasso.with(getApplicationContext()).load(image)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.logo_home)
                .error(R.drawable.logo_home).into(imgShopLogo);

        /*File file = new File(image);
        Picasso picasso = Picasso.with(getApplicationContext());
        picasso.invalidate(file);
        picasso.load(file).into(imgShopLogo);*/

        imgShopDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(drawer_page);
            }
        });

        //Shop Open Condition
        if (open.equals("1")) {
            op = getString(R.string.open);
        } else {
            op = getString(R.string.close);
        }
        // items of drawer
        items_drawer.add(new NavItem(getString(R.string.drawerReport)));
        items_drawer.add(new NavItem(getString(R.string.drawerSetting)));
        items_drawer.add(new NavItem(getString(R.string.drawerState) + op));
        items_drawer.add(new NavItem(getString(R.string.drawerUpdate)));
        items_drawer.add(new NavItem(getString(R.string.drawerChat)));
        items_drawer.add(new NavItem(getString(R.string.drawerReservation)));
        items_drawer.add(new NavItem(getString(R.string.drawerCustomerServices)));
        items_drawer.add(new NavItem(getString(R.string.drawerChangePassword)));
        items_drawer.add(new NavItem("مشاركة عبر وسائل التواصل الاجتماعي"));
        items_drawer.add(new NavItem(getString(R.string.drawerLogout)));

//        imgShopLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                webServies.shopLogout(Home.this, shop_id);
//                logoutUser();
//            }
//        });


        if (AddProduct.key == 1) {
            AddProduct.key = 0;
            android.support.v4.app.FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_dept_specific", AddProduct.department);
            bundle.putString("shop_cat_specific", AddProduct.category);
            Product product = new Product();
            product.setArguments(bundle);
            ft1.replace(R.id.fShopHomeLayout, product);
            ft1.commit();
            new AddProduct().finish();
        } else if (EditProduct.key == 1) {
            EditProduct.key = 0;
            EditProduct.flag = 0;
            android.support.v4.app.FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_dept_specific", EditProduct.edept);
            bundle.putString("shop_cat_specific", EditProduct.cat);
            Product product = new Product();
            product.setArguments(bundle);
            ft1.replace(R.id.fShopHomeLayout, product);
            ft1.commit();
            new EditProduct().finish();
        } else if (EditProductImage.key == 1) {
            EditProductImage.key = 0;
            EditProductImage.flag = 0;
            android.support.v4.app.FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_dept_specific", EditProductImage.dept);
            bundle.putString("shop_cat_specific", EditProductImage.cat);
            Product product = new Product();
            product.setArguments(bundle);
            ft1.replace(R.id.fShopHomeLayout, product);
            ft1.commit();
            new EditProductImage().finish();
        }

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        deleteCache(getApplicationContext());

        // Launching the login activity
        Intent intent = new Intent(Home.this, Login.class);
        startActivity(intent);
        finish();
    }

    //**** method for items of drawer
    private void selectItemFromDrawer(int position) {

        setTitle(items_drawer.get(position).mIcon);
        drawer_list.setItemChecked(position, true);
        setTitle(items_drawer.get(position).mIcon);

        switch (position) {
            // Report
            case 0:
                Intent report = new Intent(Home.this, Report.class);
                startActivity(report);
                break;
            // Setting
            case 1:
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                        Home.this, AlertDialog.THEME_HOLO_LIGHT);
                builderSingle.setTitle(getString(R.string.edit));

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.select_dialog_item);
                arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

                arrayAdapter.add(getString(R.string.editPData));
                arrayAdapter.add(getString(R.string.editSData));

                builderSingle.setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener() {

                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 0) {
                                    // Toast.makeText(getApplicationContext(), "P Data", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), EditRegister.class);
                                    startActivity(intent);
                                } else {
                                    // Toast.makeText(getApplicationContext(), "S Data", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), EditContinueData.class);
                                    startActivity(intent);
                                }
                            }
                        });
                builderSingle.show();
                break;
            // State
            case 2:
                Intent open = new Intent(Home.this, ShopOpen.class);
                startActivity(open);
                break;
            // Location
            case 3:
                Intent update = new Intent(Home.this, UpdateLocation.class);
                startActivity(update);
                break;

            case 4:
                //Toast.makeText(getApplicationContext(), R.string.drawerChat, Toast.LENGTH_LONG).show();
                Intent chat = new Intent(Home.this, Chat.class);
                startActivity(chat);
                break;

            case 5:
                Intent order = new Intent(Home.this, Order.class);
                startActivity(order);
                break;

            case 6:
                Toast.makeText(getApplicationContext(), R.string.drawerCustomerServices, Toast.LENGTH_LONG).show();
                break;

            case 7:
                Intent change = new Intent(Home.this, ChangePassword.class);
                startActivity(change);
                break;

            case 8:

                imgShopLogo.setDrawingCacheEnabled(true);
                imgShopLogo.buildDrawingCache();
                sharedBitmap= imgShopLogo.getDrawingCache();

                handleShareLink();

                imgShopLogo.setDrawingCacheEnabled(false); // clear drawing cache



                /*if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(image))
                            .setContentTitle(shop_name)
                            .setContentDescription(shop_address + "\n" + shop_phone)
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag("#EEM App")
                                    .build())
                            .build();
                    shareDialog.show(content);
                    ShareApi.share(content, null);
                }*/
                break;

            case 9:
                webServies.shopLogout(Home.this, shop_id);
                logoutUser();
                break;
        }

        drawer_layout.closeDrawer(drawer_page);

    }

    //Handle Share
    private void handleShareLink() {
        //Share Intent for Text Only
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"http://dasta.net/data/eem/Client/redirect_shop_profile.php/".
                concat(shop_id));
        shareIntent.setType("text/plain");

        //Share Intent for Text and Image
        if (Utility.checkPermissionREAD_EXTERNAL_STORAGE(Home.this)&&
                Utility.checkPermissionWRITE_EXTERNAL_STORAGE(Home.this)){

            final Intent imgShareIntent = new Intent(Intent.ACTION_SEND);
            imgShareIntent.setType("image/*");
            imgShareIntent.putExtra(Intent.EXTRA_STREAM,Utility.getImageUri(Home.this, sharedBitmap));
            imgShareIntent.putExtra(Intent.EXTRA_TEXT, "http://dasta.net/data/eem/Client/redirect_shop_profile.php/".
                    concat(shop_id));


            IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(Home.this, imgShareIntent, "مشاركة...",
                    new IntentPickerSheetView.OnIntentPickedListener() {
                        @Override
                        public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo) {
                            //Toast.makeText(Home.this,"FaceBook Selected",Toast.LENGTH_SHORT).show();

                            bottomSheetLayout.dismissSheet();
                           if (
                                   activityInfo.componentName.getPackageName().equals("com.facebook.orca")){
                               final Intent imgShareIntent2 = new Intent(Intent.ACTION_SEND);
                               imgShareIntent2.setType("text/plain");
                               imgShareIntent2.putExtra(Intent.EXTRA_TEXT,
                                       "http://dasta.net/data/eem/Client/redirect_shop_profile.php/".
                                               concat(shop_id));
                               startActivity(activityInfo.getConcreteIntent(imgShareIntent2));

                           }else if (activityInfo.componentName.getPackageName().equals("com.facebook.katana")){
                                /*//startActivity(activityInfo.getConcreteIntent(imgShareIntent));

                                FacebookSdk.sdkInitialize(getApplicationContext());
                                callbackManager = CallbackManager.Factory.create();

                                SharePhoto photo = new SharePhoto.Builder()
                                        .setBitmap(sharedBitmap)
                                        .build();
                                SharePhotoContent photoContent = new SharePhotoContent.Builder()
                                        .addPhoto(photo)
                                        .build();

                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setContentUrl(Uri.parse(shop_address))
                                            .setShareHashtag(new ShareHashtag.Builder()
                                                    .setHashtag("#EEM_App")
                                                    .build()).setQuote("Connect on a global scale.")
                                            .setContentDescription("Welcome To EEM App").setImageUrl(Utility.getImageUri(Home.this, sharedBitmap))
                                            .build();

                                    shareDialog.show(linkContent);*/



                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse("http://dasta.net/data/eem/Client/redirect_shop_profile.php/".
                                                concat(shop_id)))
                                        .build();
                                shareDialog.show(content);

                            }else if (activityInfo.componentName.getPackageName().equals("com.twitter.android")){

                             //   Toast.makeText(Home.this,"Twitter Selected",Toast.LENGTH_SHORT).show();
                                startActivity(activityInfo.getConcreteIntent(imgShareIntent));

                            }else{

                               // Toast.makeText(Home.this,"FaceBook,Twitter Not Selected",Toast.LENGTH_SHORT).show();
                                startActivity(activityInfo.getConcreteIntent(imgShareIntent));

                            }

                        }
                    });

            // Filter out built in sharing options such as bluetooth and beam.
            intentPickerSheet.setFilter(new IntentPickerSheetView.Filter() {

                @Override
                public boolean include(IntentPickerSheetView.ActivityInfo info) {
                    return !info.componentName.getPackageName().startsWith("com.android");
                }

            });
            // Sort activities in reverse order for no good reason
            intentPickerSheet.setSortMethod(new Comparator<IntentPickerSheetView.ActivityInfo>() {
                @Override
                public int compare(IntentPickerSheetView.ActivityInfo lhs, IntentPickerSheetView.ActivityInfo rhs) {
                    return rhs.label.compareTo(lhs.label);
                }
            });
            bottomSheetLayout.showWithSheetView(intentPickerSheet);

        }/*else {
            Utility.showDialog("السماح للتطبيق بحفظ الصور ف الذاكرة الخارجية ؟",Home.this,"android.permission.WRITE_EXTERNAL_STORAGE");
        }*/

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        if (drawer_layout.isDrawerOpen(drawer_page)) {
            drawer_layout.closeDrawer(drawer_page);
        } else if (Appointment.flag == 1) {
            Appointment.flag = 0;
            Offer.flag = 0;
            Category.flag = 0;
            LiveInfo.flag = 0;
            txtShopHomeTitle.setText(R.string.live);
            //*** Default ColorFilter
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
            ft.replace(R.id.fShopHomeLayout, home);
            ft.commit();
        } else if (Offer.flag == 1) {
            Offer.flag = 0;
            Appointment.flag = 0;
            Category.flag = 0;
            LiveInfo.flag = 0;
            txtShopHomeTitle.setText(R.string.live);
            //*** Default ColorFilter
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
            ft.replace(R.id.fShopHomeLayout, home);
            ft.commit();
        } else if (Category.flag == 1) {
            Category.flag = 0;
            Appointment.flag = 0;
            Category.flag = 0;
            LiveInfo.flag = 0;
            txtShopHomeTitle.setText(R.string.live);
            //*** Default ColorFilter
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
            ft.replace(R.id.fShopHomeLayout, home);
            ft.commit();
        } else if (LiveInfo.flag == 1) {
            txtShopHomeTitle.setText(R.string.live);
            Category.flag = 0;
            Appointment.flag = 0;
            Category.flag = 0;
            LiveInfo.flag = 0;
            //*** Default Title
            this.finish();
        } else if (AddCategory.flag1 == 1) {
            AddCategory.flag1 = 0;
            Category.flag = 1;
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
            ft.replace(R.id.fShopHomeLayout, home);
            ft.commit();
        } else if (Department.flag == 1) {
            Department.flag = 0;
            Category.flag = 1;
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
            ft.replace(R.id.fShopHomeLayout, home);
            ft.commit();
        } else if (AddDepartment.flag == 1) {
            AddDepartment.flag = 0;
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_cat_specific", AddDepartment.cat);
            Department department = new Department();
            department.setArguments(bundle);
            ft.replace(R.id.fShopHomeLayout, department);
            ft.commit();
        } else if (EditDepartment.flag == 1) {
            EditDepartment.flag = 0;
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_cat_specific", EditDepartment.cat);
            Department department = new Department();
            department.setArguments(bundle);
            ft.replace(R.id.fShopHomeLayout, department);
            ft.commit();
        } else if (Department.flag == 1) {
            Department.flag = 0;
            Category.flag = 1;
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
            ft.replace(R.id.fShopHomeLayout, home);
            ft.commit();
        } else if (Product.flag == 1) {
            Product.flag = 0;
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_cat_specific", Product.cat);
            Department department = new Department();
            department.setArguments(bundle);
            ft.replace(R.id.fShopHomeLayout, department);
            ft.commit();
        } else if (AddProduct.flag == 1) {
            AddProduct.flag = 0;
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_dept_specific", AddProduct.department);
            bundle.putString("shop_cat_specific", AddProduct.category);
            Product product = new Product();
            product.setArguments(bundle);
            ft.replace(R.id.fShopHomeLayout, product);
            ft.commit();
        } else if (EditProduct.flag == 1) {
            EditProduct.flag = 0;
            android.support.v4.app.FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_dept_specific", EditProduct.edept);
            bundle.putString("shop_cat_specific", EditProduct.cat);
            Product product = new Product();
            product.setArguments(bundle);
            ft1.replace(R.id.fShopHomeLayout, product);
            ft1.commit();
            new EditProduct().finish();
        } else if (AddOfferProduct.flag == 1) {
            AddOfferProduct.flag = 0;
            android.support.v4.app.FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_dept_specific", AddOfferProduct.dept);
            bundle.putString("shop_cat_specific", AddOfferProduct.cat);
            Product product = new Product();
            product.setArguments(bundle);
            ft1.replace(R.id.fShopHomeLayout, product);
            ft1.commit();
            new EditProduct().finish();
        } else if (AddOffer.flag == 1) {
            AddOffer.flag = 0;
            Offer.flag = 1;
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
            ft.replace(R.id.fShopHomeLayout, home);
            ft.commit();
        } else {
            super.onBackPressed();
        }
    }

    public void shopView(final String id) {

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_SELECT_VIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        try {

                            JSONObject jObj = new JSONObject(response);
                            /***  ***/
                            JSONArray data = jObj.getJSONArray("result");
                            /*** Data Return from server ***/
                            for (int i = 0; i < data.length(); i++) {

                                // Now store the user in SQLite

                                JSONObject shop = data.getJSONObject(i);

                                view = shop.getString("view");
                                txtShopView.setText(view);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                          //  Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                      //  Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("id", id);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteCache(getApplicationContext());
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            Intent intent = new Intent(context, Category.class);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
