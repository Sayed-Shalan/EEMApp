package com.dasta.eemapp.helper.Client;


public class App {

    private int mDrawable;
    private String mName;
//    private float mRating;

    public App(String name, int drawable) {
        mName = name;
        mDrawable = drawable;
//        mRating = rating;
    }

//    public float getRating() {
//        return mRating;
//    }

    public int getDrawable() {
        return mDrawable;
    }

    public String getName() {
        return mName;
    }
}

