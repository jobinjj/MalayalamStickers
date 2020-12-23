package com.techpakka.whatsappstickerspack.utils;

import android.os.Build;
import android.os.StrictMode;
import androidx.multidex.MultiDexApplication;

import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;


public class AppController extends MultiDexApplication {
    public static final boolean DEVELOPER_MODE = false;
    View footerView;
    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        if (DEVELOPER_MODE
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll().penaltyDeath().build());
        }

        mInstance = this;
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }



}