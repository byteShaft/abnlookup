package com.byteshaft.abnlookup;

import android.app.Application;

public class AppGlobals extends Application {


    public static final int ABN = 0;
    public static final int ACN = 1;
    public static final int NAME = 2;
    public static final int NAME_DETAIL = 3;
    public static int MODE = 0;

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
