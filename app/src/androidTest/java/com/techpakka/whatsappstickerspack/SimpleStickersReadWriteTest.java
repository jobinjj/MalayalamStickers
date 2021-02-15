package com.techpakka.whatsappstickerspack;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.auth.User;
import com.techpakka.whatsappstickerspack.room.Database1;
import com.techpakka.whatsappstickerspack.room.StickerPacks;
import com.techpakka.whatsappstickerspack.room.StickerPacksDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SimpleStickersReadWriteTest {
    private StickerPacksDao stickerPacksDao;
    private Database1 db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, Database1.class).build();
        stickerPacksDao = db.stickerPacksDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        StickerPacks stickerPacks = new StickerPacks();
        stickerPacks.setName("dashamoolam da");
        stickerPacks.setDownloadUrl("");
        stickerPacks.setIdentifier("");
        stickerPacks.setThumbnail("");
        stickerPacks.setTrayImageFile("");
        StickerPacks stickerPacks2 = new StickerPacks();
        stickerPacks2.setName("dashamoolam da");
        stickerPacks2.setDownloadUrl("");
        stickerPacks2.setIdentifier("");
        stickerPacks2.setThumbnail("");
        stickerPacks2.setTrayImageFile("");
        long[] stickerPacksId = stickerPacksDao.insertAll(stickerPacks,stickerPacks2);

        Log.d("RoomTest",String.valueOf(stickerPacksId[1]));
    }
}
