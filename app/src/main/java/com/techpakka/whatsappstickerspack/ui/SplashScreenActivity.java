package com.techpakka.whatsappstickerspack.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.DataStoreChannelEventName;
import com.amplifyframework.datastore.events.NetworkStatusEvent;
import com.amplifyframework.datastore.generated.model.StickerPacks;
import com.amplifyframework.datastore.generated.model.Stickers;
import com.amplifyframework.hub.HubChannel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.idoideas.DataArchiver;
import com.techpakka.whatsappstickerspack.utils.Constants;
import com.techpakka.whatsappstickerspack.utils.DownloadStickersFromURL;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashScreenActivity extends AppCompatActivity implements DownloadStickersFromURL.StickersDownloadListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private long token;
    private ProgressBar progressBar;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = findViewById(R.id.progressBar);
        status = findViewById(R.id.downloadStatus);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        Amplify.DataStore.start(
                () -> Log.i("MyAmplifyApp", "DataStore started"),
                error -> Log.e("MyAmplifyApp", "Error starting DataStore", error)
        );
        Amplify.Hub.subscribe(
                HubChannel.DATASTORE,
                hubEvent -> {
                    if(DataStoreChannelEventName.SYNC_QUERIES_READY.toString().equals(hubEvent.getName())){
                        Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },3000);
    }

    private boolean zipFileExists() {
        File file = new File(getFilesDir() + File.separator + "stickers/stickers.zip");
        return file.exists();
    }


    private boolean Authenticated() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (Constants.isNetworkConnected(this)) {
            if (user == null) {
                return signInAnonymously(auth);
            } else {
                return true;
            }
        } else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
            //showRefreshButton();
        }
        return false;
    }

    private boolean signInAnonymously(FirebaseAuth auth) {
        // showProgressBar();
        // [START signin_anonymously]
        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //getStickersFromFirebase();
                            Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                            //updateUI(user);
                        } else {

                            Toast.makeText(SplashScreenActivity.this, "Something went wrong please refresh",
                                    Toast.LENGTH_SHORT).show();

                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        // hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]
        return false;
    }

    private void getStickersFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("malayalamstickerfiles").document("stickers");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    try {
                        if (document != null){
                            try {
                                new DownloadStickersFromURL(document.getString("file_size"),SplashScreenActivity.this,new URL(document.getString("url")),SplashScreenActivity.this).execute();
                                DataArchiver.writeToSharedPrefs(document.getString("json"),SplashScreenActivity.this);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                } else {
                    showServerError();
                }
            }
        });
    }

    private void showServerError() {
       // Toast.makeText(this, "cannot fetch data", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDownloadStarted() {
    }

    @Override
    public void onFailed(String error) {
    //    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadProgress(int progress) {
        Thread thread = new Thread(){
            public void run(){
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
        };
        thread.start();
    }

    @Override
    public void onDownloadCompleted() {
        Thread thread = new Thread(){
            public void run(){
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setProgress(0);
                        status.setText("Extracting...");
                    }
                });
            }
        };
        thread.start();

        new DataArchiver.extractZip(this, getFilesDir() + File.separator + "stickers/", getFilesDir() + "/stickers/stickers.zip", new DataArchiver.zipListener() {
            @Override
            public void onExtracted() {
                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();            }

            @Override
            public void onExtractionFailed() {
                Thread thread = new Thread(){
                    public void run(){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SplashScreenActivity.this, "failed to extract", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };
                thread.start();
            }

            @Override
            public void onExtractProgress(int progress) {
                Thread thread = new Thread(){
                    public void run(){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progress);
                            }
                        });
                    }
                };
                thread.start();
            }
        }).execute();

    }

    @Override
    public void onCancelled() {
        System.out.println("cancelled");
    }
}