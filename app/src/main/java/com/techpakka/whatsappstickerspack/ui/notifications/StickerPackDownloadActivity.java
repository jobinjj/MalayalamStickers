package com.techpakka.whatsappstickerspack.ui.notifications;

import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.idoideas.DataArchiver;
import com.techpakka.whatsappstickerspack.ui.MarketStickerPreviewAdapter;
import com.techpakka.whatsappstickerspack.utils.DownloadStickersFromURL;
import com.techpakka.whatsappstickerspack.whatsappbasecode.AddStickerPackActivity;
import com.techpakka.whatsappstickerspack.whatsappbasecode.StickerPreviewAdapter;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.Sticker;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StickerPackDownloadActivity extends AddStickerPackActivity {

    /**
     * Do not change below values of below 3 lines as this is also used by WhatsApp
     */
    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";

    public static final String EXTRA_STICKER_PACK_WEBSITE = "sticker_pack_website";
    public static final String EXTRA_STICKER_PACK_EMAIL = "sticker_pack_email";
    public static final String EXTRA_STICKER_PACK_PRIVACY_POLICY = "sticker_pack_privacy_policy";
    public static final String EXTRA_STICKER_PACK_LICENSE_AGREEMENT = "sticker_pack_license_agreement";
    public static final String EXTRA_STICKER_PACK_TRAY_ICON = "sticker_pack_tray_icon";
    public static final String EXTRA_SHOW_UP_BUTTON = "show_up_button";
    public static final String EXTRA_STICKER_PACK_DATA = "stickerPack";

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private MarketStickerPreviewAdapter marketStickerPreviewAdapter;
    private int numColumns;
    private StickerPack stickerPack;
    private View divider;
    private ConstraintLayout downloadContainer;
    private DownloadStickersFromURL task;
    private ProgressBar progressBar;
    private TextView txtProgress;
    private TextView txtSize;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack_download);
        boolean showUpButton = getIntent().getBooleanExtra(EXTRA_SHOW_UP_BUTTON, false);
        stickerPack = getIntent().getParcelableExtra(EXTRA_STICKER_PACK_DATA);

        TextView packNameTextView = findViewById(R.id.pack_name);
        TextView packPublisherTextView = findViewById(R.id.author);
        ImageView packTrayIcon = findViewById(R.id.tray_image);
        TextView packSizeTextView = findViewById(R.id.pack_size);
        progressBar = findViewById(R.id.progressBar3);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        txtProgress = findViewById(R.id.textView);
        downloadButton = findViewById(R.id.downloadButton);
        txtProgress.setText("0% 0f " + Formatter.formatShortFileSize(this, stickerPack.getTotalSize()));
        if (stickerPackAlreadyDownloaded(stickerPack)){
            downloadButton.setText("Downloaded");
            downloadButton.setEnabled(false);
        }
        downloadContainer = findViewById(R.id.download_container);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView = findViewById(R.id.sticker_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(pageLayoutListener);
        recyclerView.addOnScrollListener(dividerScrollListener);
        divider = findViewById(R.id.divider);
        if (marketStickerPreviewAdapter == null) {
            marketStickerPreviewAdapter = new MarketStickerPreviewAdapter(getLayoutInflater(), R.drawable.sticker_error, getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size), getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_padding), stickerPack,this);
            recyclerView.setAdapter(marketStickerPreviewAdapter);
        }
        packNameTextView.setText(stickerPack.name);
        packPublisherTextView.setText(stickerPack.publisher);
        Glide.with(this).load(stickerPack.getThumbnail()).into(packTrayIcon);
        packSizeTextView.setText(Formatter.formatShortFileSize(this, stickerPack.getTotalSize()));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showUpButton);
            getSupportActionBar().setTitle(showUpButton ? getResources().getString(R.string.title_activity_sticker_pack_details_multiple_pack) : getResources().getQuantityString(R.plurals.title_activity_sticker_packs_list, 1));
        }
    }

    private boolean stickerPackAlreadyDownloaded(StickerPack stickerPack) {

        List<StickerPack> localStickerList = DataArchiver.readStickerPackJSONFromSharedPref(this);
        for (StickerPack localStickerPack:
             localStickerList) {
            if (localStickerPack.getIdentifier().equals(stickerPack.identifier)) return true;
        }
        return false;
    }


    private final ViewTreeObserver.OnGlobalLayoutListener pageLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            setNumColumns(recyclerView.getWidth() / recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size));
        }
    };

    private void setNumColumns(int numColumns) {
        if (this.numColumns != numColumns) {
            layoutManager.setSpanCount(numColumns);
            this.numColumns = numColumns;
            if (marketStickerPreviewAdapter != null) {
                marketStickerPreviewAdapter.notifyDataSetChanged();
            }
        }
    }

    private final RecyclerView.OnScrollListener dividerScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            updateDivider(recyclerView);
        }

        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            updateDivider(recyclerView);
        }

        private void updateDivider(RecyclerView recyclerView) {
            boolean showDivider = recyclerView.computeVerticalScrollOffset() > 0;
            if (divider != null) {
                divider.setVisibility(showDivider ? View.VISIBLE : View.INVISIBLE);
            }
        }
    };


    public void downloadSticker(View view) {
        downloadContainer.setVisibility(View.VISIBLE);
        ((Button) view).setText("Downloading");
        ((Button) view).setEnabled(false);
        try {
            task = new DownloadStickersFromURL(String.valueOf(stickerPack.getTotalSize()), new DownloadStickersFromURL.StickersDownloadListener() {
                @Override
                public void onDownloadStarted() {

                }

                @Override
                public void onFailed(String error) {

                }

                @Override
                public void onDownloadProgress(int progress) {
                    Thread thread = new Thread(){
                        public void run(){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtProgress.setText( String.valueOf(progress) + "% 0f " + Formatter.formatShortFileSize(StickerPackDownloadActivity.this, stickerPack.getTotalSize()));
                                    progressBar.setProgress(progress);
                                }
                            });
                        }
                    };
                    thread.start();
                }

                @Override
                public void onDownloadCompleted() {
                    Thread thread = new Thread(){
                        public void run(){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(0);
                                    txtProgress.setText("Extracting...");
                                }
                            });
                        }
                    };
                    thread.start();
                    new DataArchiver.extractZip(StickerPackDownloadActivity.this, getFilesDir() + File.separator + "stickers/", getFilesDir() + "/stickers/stickers.zip", new DataArchiver.zipListener() {
                        @Override
                        public void onExtracted() {
                            Thread thread = new Thread(){
                                public void run(){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            downloadButton.setText("Downloaded");
                                            downloadButton.setEnabled(false);
                                            downloadContainer.setVisibility(View.GONE);
                                            saveStickerToLocalStickers();
                                        }
                                    });
                                }

                                private void saveStickerToLocalStickers() {
                                    ArrayList<StickerPack> localStickers = DataArchiver.readStickerPackJSONFromSharedPref(StickerPackDownloadActivity.this);
                              /*      for (Sticker sticker :
                                            stickerPack.getStickers()) {
                                        String imagePath = sticker.getImageFileName();
                                        sticker.setImageFileName(imagePath.substring(imagePath.lastIndexOf("/")+1));
                                    }*/
                                    localStickers.add(stickerPack);
                                    DataArchiver.writeStickerBookJSON(localStickers,StickerPackDownloadActivity.this);
                                }
                            };
                            thread.start();
                        }

                        @Override
                        public void onExtractionFailed() {
                            Thread thread = new Thread(){
                                public void run(){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(StickerPackDownloadActivity.this, "failed to extract", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            };
                            thread.start();
                        }

                        @Override
                        public void onExtractProgress(int progress) {
                            Thread thread = new Thread(){
                                public void run(){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progressBar.setProgress(progress);
                                        }
                                    });
                                }
                            };
                            thread.start();
                        }
                    }).execute();
                }

                @Override
                public void onCancelled() {

                }
            },new URL(stickerPack.downloadUrl),StickerPackDownloadActivity.this);
            task.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void cancelDownload(View view) {
        if (task != null) task.cancel(true);
    }
}