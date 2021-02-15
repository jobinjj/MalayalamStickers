package com.techpakka.whatsappstickerspack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.DocumentTransform;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StickerUploaderActivity extends AppCompatActivity {

    private EditText txtStickerPackName;
    private EditText editTextNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_uploader);
        txtStickerPackName = findViewById(R.id.txtStickerPackName);
        editTextNumber = findViewById(R.id.editTextNumber);
    }
    public void addSticker(View view) {
        if (txtStickerPackName.getText().toString().isEmpty()){
            txtStickerPackName.setError("Enter stickerpack name");
            return;
        }
        if (editTextNumber.getText().toString().isEmpty()){
            editTextNumber.setError("Enter sticker amount");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> stickerPack = new HashMap<>();
        stickerPack.put("downloadUrl", "");
        stickerPack.put("fileSize", "");
        stickerPack.put("identifier", "");
        stickerPack.put("name", "");
        stickerPack.put("thumbnail", "");
        stickerPack.put("trayImageFile", "");
        stickerPack.put("time_stamp", new Timestamp(new Date()));

// Add a new document with a generated ID
        db.collection("Stickerpacks").document(txtStickerPackName.getText().toString())
                .set(stickerPack)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        for (int i = 0; i < Integer.parseInt(editTextNumber.getText().toString()); i++) {

                            Map<String, Object> sticker = new HashMap<>();
                            sticker.put("imageFileName", txtStickerPackName.getText().toString() + "_0" + String.valueOf(i+1));
                            sticker.put("size", "");
                            sticker.put("stickerUrl", "");
                            sticker.put("stickerPack", txtStickerPackName.getText().toString());

                            db.collection("Stickers").document()
                                    .set(sticker)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }

                        }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}