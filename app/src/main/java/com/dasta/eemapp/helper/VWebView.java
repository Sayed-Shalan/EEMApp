package com.dasta.eemapp.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by Mohamed on 23/03/2018.
 */

public class VWebView extends WebView {
    final boolean topDown = true;

    public VWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        if (topDown) {
            canvas.translate(getHeight(), 0);
            canvas.rotate(90);
        } else {
            canvas.translate(0, getWidth());
            canvas.rotate(-90);
        }
        canvas.clipRect(0, 0, getWidth(), getHeight(), android.graphics.Region.Op.REPLACE);
        super.draw(canvas);
    }
}