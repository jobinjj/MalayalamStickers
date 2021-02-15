package com.techpakka.whatsappstickerspack.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Stickers {
    @PrimaryKey(autoGenerate = true)
    public long stickerId;

    @ColumnInfo(name = "size")
    public long size;

    @ColumnInfo(name = "image_file_name")
    public String imageFileName;

    @ColumnInfo(name = "sticker_url")
    public String stickerUrl;

    @ColumnInfo(name = "emojis")
    public String emojis = "";

    @ColumnInfo(name = "sticker_pack_id")
    public long stickerPackId;

    public long getStickerId() {
        return stickerId;
    }

    public void setStickerId(long stickerId) {
        this.stickerId = stickerId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getStickerUrl() {
        return stickerUrl;
    }

    public void setStickerUrl(String stickerUrl) {
        this.stickerUrl = stickerUrl;
    }

    public String getEmojis() {
        return emojis;
    }

    public void setEmojis(String emojis) {
        this.emojis = emojis;
    }

    public long getStickerPackId() {
        return stickerPackId;
    }

    public void setStickerPackId(long stickerPackId) {
        this.stickerPackId = stickerPackId;
    }
}
