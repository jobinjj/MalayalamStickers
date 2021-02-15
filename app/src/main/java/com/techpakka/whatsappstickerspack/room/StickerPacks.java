package com.techpakka.whatsappstickerspack.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StickerPacks {
    @PrimaryKey(autoGenerate = true)
    public long stickerPackId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "identifier")
    public String identifier;

    @ColumnInfo(name = "tray_image_file")
    public String trayImageFile;

    @ColumnInfo(name = "thumbnail")
    public String thumbnail;

    @ColumnInfo(name = "download_url")
    public String downloadUrl;

    public long getStickerPackId() {
        return stickerPackId;
    }

    public void setStickerPackId(long stickerPackId) {
        this.stickerPackId = stickerPackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTrayImageFile() {
        return trayImageFile;
    }

    public void setTrayImageFile(String trayImageFile) {
        this.trayImageFile = trayImageFile;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
