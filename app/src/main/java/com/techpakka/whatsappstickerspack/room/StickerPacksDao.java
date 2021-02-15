package com.techpakka.whatsappstickerspack.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import java.util.List;

@Dao
public interface StickerPacksDao {
    @Query("SELECT * FROM stickerpacks")
    List<StickerPacks> getAll();

/*    @Query("SELECT * FROM stickerpack WHERE stickerPackId IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);*/

/*
    @Query("SELECT * FROM stickerpack WHERE name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);
*/

    @Query("SELECT * FROM stickerpacks WHERE stickerPackId == :stickerPackId")
    StickerPacks getStickerPackWithId(long stickerPackId);

    @Insert
    long[] insertAll(StickerPacks... stickerPacks);

    @Insert
    long insert(StickerPacks stickerPacks);

    @Delete
    void delete(StickerPacks stickerPacks);

    @Query("DELETE FROM stickerpacks")
    void clearTable();
}
