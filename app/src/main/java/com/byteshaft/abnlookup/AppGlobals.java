package com.byteshaft.abnlookup;

import android.app.Application;
import android.graphics.Typeface;

public class AppGlobals extends Application {


    public static final int ABN = 0;
    public static final int ACN = 1;
    public static final int NAME = 2;
    public static final int NAME_DETAIL = 3;
    public static int MODE = 0;
    public static Typeface robotoRegular;
    public static Typeface robotoItalic;
    public static Typeface bold;

    @Override
    public void onCreate() {
        super.onCreate();
        robotoRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto_Regular.ttf");
        robotoItalic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto_Italic.ttf");
        bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto_Bold.ttf");

    }
}
