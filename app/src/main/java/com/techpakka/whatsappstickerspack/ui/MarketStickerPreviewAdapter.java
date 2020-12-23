/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.techpakka.whatsappstickerspack.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Stickers;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.Sticker;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import java.util.ArrayList;
import java.util.List;


public class MarketStickerPreviewAdapter extends RecyclerView.Adapter<MarketStickerPreviewAdapter.StickerPreviewViewHolder> {

    @NonNull
    private final List<Sticker> stickers;

    private final int cellSize;
    private final int cellLimit;
    private final int cellPadding;
    private final int errorResource;
    private Context context;

    private final LayoutInflater layoutInflater;

    public MarketStickerPreviewAdapter(
            @NonNull List<Sticker> stickers, @NonNull final LayoutInflater layoutInflater,
            final int errorResource,
            final int cellSize,
            final int cellPadding,
            Context context) {
        this.stickers = stickers;
        this.cellSize = cellSize;
        this.cellPadding = cellPadding;
        this.cellLimit = 0;
        this.layoutInflater = layoutInflater;
        this.errorResource = errorResource;
    this.context = context;
    }

    @NonNull
    @Override
    public StickerPreviewViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        View itemView = layoutInflater.inflate(R.layout.sticker_image_item, viewGroup, false);
        StickerPreviewViewHolder vh = new StickerPreviewViewHolder(itemView);

        ViewGroup.LayoutParams layoutParams = vh.stickerPreviewView.getLayoutParams();
        layoutParams.height = cellSize;
        layoutParams.width = cellSize;
        vh.stickerPreviewView.setLayoutParams(layoutParams);
        vh.stickerPreviewView.setPadding(cellPadding, cellPadding, cellPadding, cellPadding);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final StickerPreviewViewHolder stickerPreviewViewHolder, final int i) {
        stickerPreviewViewHolder.stickerPreviewView.setImageResource(errorResource);
        //stickerPreviewViewHolder.stickerPreviewView.setImageURI(ImageManipulation.getImageUri(stickerPack.getStickers().get(i).imageFileName,context));
        Glide.with(context.getApplicationContext()).load(stickers.get(i).getStickerUrl()).into(stickerPreviewViewHolder.stickerPreviewView);
    }

    @Override
    public int getItemCount() {
        int numberOfPreviewImagesInPack;
        numberOfPreviewImagesInPack = stickers.size();
        if (cellLimit > 0) {
            return Math.min(numberOfPreviewImagesInPack, cellLimit);
        }
        return numberOfPreviewImagesInPack;
    }

    class StickerPreviewViewHolder extends RecyclerView.ViewHolder {

        final SimpleDraweeView stickerPreviewView;

        StickerPreviewViewHolder(final View itemView) {
            super(itemView);
            stickerPreviewView = itemView.findViewById(R.id.sticker_preview);
        }
    }
}
