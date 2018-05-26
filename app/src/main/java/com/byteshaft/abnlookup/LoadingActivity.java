package com.byteshaft.abnlookup;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.github.ybq.android.spinkit.SpinKitView;

public class LoadingActivity extends Activity {

    private static LoadingActivity instance;
    private SpinKitView spinKitView;


    public static LoadingActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.loading_screen);
        spinKitView = findViewById(R.id.spin_kit);
        spinKitView.setIndeterminate(true);
        instance = this;
    }
}
