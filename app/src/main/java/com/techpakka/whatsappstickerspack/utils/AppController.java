package com.techpakka.whatsappstickerspack.utils;

import android.os.Build;
import android.os.StrictMode;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.techpakka.whatsappstickerspack.room.Database1;
import com.techpakka.whatsappstickerspack.room.StickerPacksDao;
import com.techpakka.whatsappstickerspack.room.StickersDao;


public class AppController extends MultiDexApplication {
    public static final boolean DEVELOPER_MODE = false;
    View footerView;
    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;
    public static Database1 db;
    public static StickerPacksDao stickerPacksDao;
    public static StickersDao stickersDao;

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
        db = Room.databaseBuilder(getApplicationContext(),
                Database1.class, "Database1").build();
        stickerPacksDao = db.stickerPacksDao();
        stickersDao = db.stickersDao();
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }



}