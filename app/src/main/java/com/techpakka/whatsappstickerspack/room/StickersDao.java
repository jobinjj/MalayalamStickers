package com.techpakka.whatsappstickerspack.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StickersDao {
    @Query("SELECT * FROM stickers")
    List<Stickers> getAll();

/*    @Query("SELECT * FROM stickerpack WHERE stickerPackId IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);*/

/*
    @Query("SELECT * FROM stickerpack WHERE name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);
*/

    @Query("SELECT * FROM stickers WHERE sticker_pack_id IN (:stickerPackId)")
    List<Stickers> getAllStickersWithId(long stickerPackId);

    @Insert
    void insertAll(Stickers... stickers);

    @Insert
    long insert(Stickers stickers);

    @Delete
    void delete(Stickers stickers);

    @Query("DELETE FROM stickers")
    void clearTable();
}
