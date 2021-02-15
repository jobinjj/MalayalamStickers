package com.techpakka.whatsappstickerspack.room;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techpakka.whatsappstickerspack.R;

import java.util.List;

public class RoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        LinearLayout parent = findViewById(R.id.parent);

/*        new RoomTasks.FetchStickerPacks(new RoomTasks.FetchStickerPacks.fetchStickerPacksListener() {
            @Override
            public void onFetched(List<StickerPacks> stickerPacks) {
                for (StickerPacks stickerPack:
                     stickerPacks) {
                    TextView textView = new TextView(RoomActivity.this);
                    textView.setText(String.valueOf(stickerPack.getName()));
                    parent.addView(textView);
                }
            }
        }).execute();*/

    }
}