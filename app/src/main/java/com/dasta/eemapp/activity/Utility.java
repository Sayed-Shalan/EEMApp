package com.dasta.eemapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.AppController;
import com.dasta.eemapp.helper.Chat;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Utility {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    //Crop a bitmap and returns a circular bitmap
    public static Bitmap getCircularBitmap(Bitmap srcBitmap) {
        // Calculate the circular bitmap width with border
        int squareBitmapWidth = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());

        // Initialize a new instance of Bitmap
        Bitmap dstBitmap = Bitmap.createBitmap(
                squareBitmapWidth, // Width
                squareBitmapWidth, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        /*
            Canvas
                The Canvas class holds the "draw" calls. To draw something, you need 4 basic
                components: A Bitmap to hold the pixels, a Canvas to host the draw calls (writing
                into the bitmap), a drawing primitive (e.g. Rect, Path, text, Bitmap), and a paint
                (to describe the colors and styles for the drawing).
        */
        // Initialize a new Canvas to draw circular bitmap
        Canvas canvas = new Canvas(dstBitmap);

        // Initialize a new Paint instance
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        /*
            Rect
                Rect holds four integer coordinates for a rectangle. The rectangle is represented by
                the coordinates of its 4 edges (left, top, right bottom). These fields can be accessed
                directly. Use width() and height() to retrieve the rectangle's width and height.
                Note: most methods do not check to see that the coordinates are sorted correctly
                (i.e. left <= right and top <= bottom).
        */
        /*
            Rect(int left, int top, int right, int bottom)
                Create a new rectangle with the specified coordinates.
        */
        // Initialize a new Rect instance
        Rect rect = new Rect(0, 0, squareBitmapWidth, squareBitmapWidth);

        /*
            RectF
                RectF holds four float coordinates for a rectangle. The rectangle is represented by
                the coordinates of its 4 edges (left, top, right bottom). These fields can be
                accessed directly. Use width() and height() to retrieve the rectangle's width and
                height. Note: most methods do not check to see that the coordinates are sorted
                correctly (i.e. left <= right and top <= bottom).
        */
        // Initialize a new RectF instance
        RectF rectF = new RectF(rect);

        /*
            public void drawOval (RectF oval, Paint paint)
                Draw the specified oval using the specified paint. The oval will be filled or
                framed based on the Style in the paint.

            Parameters
                oval : The rectangle bounds of the oval to be drawn

        */
        // Draw an oval shape on Canvas
        canvas.drawOval(rectF, paint);

        /*
            public Xfermode setXfermode (Xfermode xfermode)
                Set or clear the xfermode object.
                Pass null to clear any previous xfermode. As a convenience, the parameter passed
                is also returned.

            Parameters
                xfermode : May be null. The xfermode to be installed in the paint
            Returns
                xfermode
        */
        /*
            public PorterDuffXfermode (PorterDuff.Mode mode)
                Create an xfermode that uses the specified porter-duff mode.

            Parameters
                mode : The porter-duff mode that is applied

        */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // Calculate the left and top of copied bitmap
        float left = (squareBitmapWidth-srcBitmap.getWidth())/2;
        float top = (squareBitmapWidth-srcBitmap.getHeight())/2;

        /*
            public void drawBitmap (Bitmap bitmap, float left, float top, Paint paint)
                Draw the specified bitmap, with its top/left corner at (x,y), using the specified
                paint, transformed by the current matrix.

                Note: if the paint contains a maskfilter that generates a mask which extends beyond
                the bitmap's original width/height (e.g. BlurMaskFilter), then the bitmap will be
                drawn as if it were in a Shader with CLAMP mode. Thus the color outside of the

                original width/height will be the edge color replicated.

                If the bitmap and canvas have different densities, this function will take care of
                automatically scaling the bitmap to draw at the same density as the canvas.

            Parameters
                bitmap : The bitmap to be drawn
                left : The position of the left side of the bitmap being drawn
                top : The position of the top side of the bitmap being drawn
                paint : The paint used to draw the bitmap (may be null)
        */
        // Make a rounded image by copying at the exact center position of source image
        canvas.drawBitmap(srcBitmap, left, top, paint);

        // Free the native object associated with this bitmap.
        srcBitmap.recycle();

        // Return the circular bitmap
        return dstBitmap;
    }

    // Custom method to add a border around circular bitmap
    public static Bitmap addBorderToCircularBitmap(Bitmap srcBitmap, int borderWidth, int borderColor){
        // Calculate the circular bitmap width with border
        int dstBitmapWidth = srcBitmap.getWidth()+borderWidth*2;

        // Initialize a new Bitmap to make it bordered circular bitmap
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth,dstBitmapWidth, Bitmap.Config.ARGB_8888);

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(dstBitmap);
        // Draw source bitmap to canvas
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);

        // Initialize a new Paint instance to draw border
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);

        /*
            public void drawCircle (float cx, float cy, float radius, Paint paint)
                Draw the specified circle using the specified paint. If radius is <= 0, then nothing
                will be drawn. The circle will be filled or framed based on the Style in the paint.

            Parameters
                cx : The x-coordinate of the center of the cirle to be drawn
                cy : The y-coordinate of the center of the cirle to be drawn
                radius : The radius of the cirle to be drawn
                paint : The paint used to draw the circle
        */
        // Draw the circular border around circular bitmap
        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getWidth() / 2, // cy
                canvas.getWidth()/2 - borderWidth / 2, // Radius
                paint // Paint
        );

        // Free the native object associated with this bitmap.
        srcBitmap.recycle();

        // Return the bordered circular bitmap
        return dstBitmap;
    }


    //Convert String to Date
    public static Date convertToDate2(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    // Custom method to add a shadow around circular bitmap
    public static Bitmap addShadowToCircularBitmap(Bitmap srcBitmap, int shadowWidth, int shadowColor){
        // Calculate the circular bitmap width with shadow
        int dstBitmapWidth = srcBitmap.getWidth()+shadowWidth*2;
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth,dstBitmapWidth, Bitmap.Config.ARGB_8888);

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawBitmap(srcBitmap, shadowWidth, shadowWidth, null);

        // Paint to draw circular bitmap shadow
        Paint paint = new Paint();
        paint.setColor(shadowColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(shadowWidth);
        paint.setAntiAlias(true);

        // Draw the shadow around circular bitmap
        canvas.drawCircle(
                dstBitmapWidth / 2, // cx
                dstBitmapWidth / 2, // cy
                dstBitmapWidth / 2 - shadowWidth / 2, // Radius
                paint // Paint
        );

        /*
            public void recycle ()
                Free the native object associated with this bitmap, and clear the reference to the
                pixel data. This will not free the pixel data synchronously; it simply allows it to
                be garbage collected if there are no other references. The bitmap is marked as
                "dead", meaning it will throw an exception if getPixels() or setPixels() is called,
                and will draw nothing. This operation cannot be reversed, so it should only be
                called if you are sure there are no further uses for the bitmap. This is an advanced
                call, and normally need not be called, since the normal GC process will free up this
                memory when there are no more references to this bitmap.
        */
        srcBitmap.recycle();

        // Return the circular bitmap with shadow
        return dstBitmap;
    }

    public static String getDayName(String dateStr){

        Locale locale = new Locale("ar");

        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = inFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE",locale);
        return outFormat.format(date);
    }

    public static String getMonth(String date) throws ParseException{
        Locale locale = new Locale("ar");
        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMMM",locale).format(cal.getTime());
        return monthName;
    }

    public static String getDateNotification(Context context, Chat chat){
        String dateStr="";
        Locale locale = new Locale("en");
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMdd_HHmmss",locale);

        Date current = new Date();
        String current_date = timeStampFormat.format(current);

        long millis=Utility.convertToDate2(current_date).getTime()-Utility.convertToDate2(chat.getDateTime()).getTime();
        int second= (int) (millis/1000);
        int min= (int) ((double)second/60);
        int hours=(int) ((double)min/60);
        int days=(int) ((double)hours/24);
        Log.e("Milis",millis+"");
        Log.e("Seconds",second+"");
        Log.e("Minutes",min+"");
        Log.e("Hours",hours+"");
        Log.e("Days",days+"");


        if (second<=0){
            dateStr=dateStr.concat(context.getResources().getString(R.string.now));
        }else if (second>0&&second<=59){
            if (second==1){
                dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ").concat(context.getResources().getString(R.string.one_second)));
            }else if (second==2){
                dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ").concat(context.getResources().getString(R.string.two_seconds)));
            }else if (second<=10){
                dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ")
                        .concat(String.valueOf(second)).concat(" ").concat(context.getResources().getString(R.string.seconds)));
            }else {
                dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ")
                        .concat(String.valueOf(second)).concat(" ").concat(context.getResources().getString(R.string.one_second)));
            }
        }else {
            if (min >= 1 && min <= 59) {
                if (min == 1) {
                    dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ").concat(context.getResources().getString(R.string.minute)));
                } else if (min == 2) {
                    dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ").concat(context.getResources().getString(R.string.two_minutes)));
                } else if (min <= 10) {
                    dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ")
                            .concat(String.valueOf(min)).concat(" ").concat(context.getResources().getString(R.string.minutes)));
                } else {
                    dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ")
                            .concat(String.valueOf(min)).concat(" ").concat(context.getResources().getString(R.string.minute)));
                }
            } else {

                if (hours >= 1 && hours <= 23) {
                    if (hours == 1) {
                        dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ").
                                concat(context.getResources().getString(R.string.hour)));

                    } else if (hours == 2) {
                        dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ").
                                concat(context.getResources().getString(R.string.two_hours)));
                    } else if (hours <= 10) {
                        dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ")
                                .concat(String.valueOf(hours)).concat(" ").concat(context.getResources().getString(R.string.hours)));
                    } else {
                        dateStr=dateStr.concat(context.getResources().getString(R.string.since).concat(" ")
                                .concat(String.valueOf(hours)).concat(" ").concat(context.getResources().getString(R.string.hour)));
                    }

                } else {
                    if (days == 1) {
                        dateStr=dateStr.concat(context.getResources().getString(R.string.yesterday).concat(" ").
                                concat(context.getResources().getString(R.string.in).concat(" ")).
                                concat(chat.getDateTime().substring(11, 16)));
                    } else if (days <= 7) {
                        dateStr=dateStr.concat(
                                Utility.getDayName(chat.getDateTime().substring(0, 10)).concat(" ").
                                        concat(context.getResources().getString(R.string.in))
                                        .concat(" ").
                                        concat(chat.getDateTime().substring(11, 16)));
                    } else {
                        try {
                            dateStr=dateStr.concat(chat.getDateTime().substring(8, 10).concat(" ").concat(
                                    Utility.getMonth(chat.getDateTime().substring(0, 10))).concat(" ").
                                    concat(context.getResources().getString(R.string.in))
                                    .concat(" ").
                                            concat(chat.getDateTime().substring(11, 16)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return dateStr;
    }

    //Convert String to Date
    public static Date convertToDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    //Check Permission
    public static boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    //Check Permission
    public static boolean checkPermissionWRITE_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }


    //Get Bitmap Uri which is registered at external storage
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver()
                , inImage, "Title", null);
        return Uri.parse(path);
    }

    //Show Dialog
    public static void showDialog(final String msg, final Context context,
                                  final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    //send message notification
    public static void sendMessageNotification(final String sender_id,final String sender_name,final String receiver_id,final String receiver_name,
                                               final String msg,final String dateTime,final String type,final String token){

        StringRequest sendNotificationReq=new StringRequest(Request.Method.POST, "http://dasta.net/data/eem/Chat/push_notification.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<>();
                map.put("sender_id",sender_id);
                map.put("sender_name",sender_name);
                map.put("receiver_id",receiver_id);
                map.put("receiver_name",receiver_name);
                map.put("msg",msg);
                map.put("type",type);
                map.put("token",token);
                map.put("dateTime",dateTime);
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(sendNotificationReq);

    }
}
