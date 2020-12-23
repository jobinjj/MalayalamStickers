package com.techpakka.whatsappstickerspack.utils;

import android.os.Build;
import android.os.StrictMode;
import androidx.multidex.MultiDexApplication;

import android.util.Log;
import android.view.View;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
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
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());
              Log.i("Amplify","Inititalized amplify");
        } catch (AmplifyException e) {
            e.printStackTrace();
            Log.e("Amplify","Could not initialize Amplify",e);
        }
        mInstance = this;
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }



}