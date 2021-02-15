package com.techpakka.whatsappstickerspack.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.idoideas.DataArchiver;
import com.techpakka.whatsappstickerspack.room.RoomTasks;
import com.techpakka.whatsappstickerspack.room.StickerPacks;
import com.techpakka.whatsappstickerspack.room.Stickers;
import com.techpakka.whatsappstickerspack.utils.AppController;
import com.techpakka.whatsappstickerspack.utils.Constants;
import com.techpakka.whatsappstickerspack.utils.DownloadStickersFromURL;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.Sticker;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity{

    private ProgressBar progressBar;
    private TextView status;
    public static ArrayList<StickerPack> serverStickerPacks = new ArrayList<>();
    public static MutableLiveData<ArrayList<StickerPack>> mutableStickerPacks = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = findViewById(R.id.progressBar);
        status = findViewById(R.id.downloadStatus);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        if (Authenticated()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            },2000);
            }
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


}