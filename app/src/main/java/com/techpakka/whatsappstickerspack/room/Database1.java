package com.techpakka.whatsappstickerspack.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StickerPacks.class,Stickers.class}, version = 1)
public abstract class Database1 extends RoomDatabase {
    public abstract StickerPacksDao stickerPacksDao();
    public abstract StickersDao stickersDao();
}
