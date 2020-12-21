/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.techpakka.whatsappstickerspack.whatsappbasecode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.idoideas.DataArchiver;
import com.techpakka.whatsappstickerspack.idoideas.StickerBook;
import com.techpakka.whatsappstickerspack.ui.StickerPackDetailsActivity;
import com.techpakka.whatsappstickerspack.ui.StickerPackListFragment;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class EntryActivity extends AppCompatActivity {
    private View progressBar;
    private LoadListAsyncTask loadListAsyncTask;
    private ArrayList<StickerPack> stickerPackList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        overridePendingTransition(0, 0);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressBar = findViewById(R.id.entry_activity_progress);
        loadListAsyncTask = new LoadListAsyncTask(this);
        //loadListAsyncTask.execute();
        //getStickersFromServer();



        Fresco.initialize(this);

        StickerBook.init(this);
        stickerPackList = StickerBook.getAllStickerPacks();
     /*   if (stickerPackList.size() > 2){
            for (int i = stickerPackList.size() - 1; i > 2; i--) {
                stickerPackList.remove(i);
            }
        }*/
        showStickerPack(stickerPackList);
    }

    private void getStickersFromServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://keralabusmods.netlify.app/api/stickers.txt",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        DataArchiver.writeStickerBookJSON(stickerPackList,EntryActivity.this);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }


    private void showStickerPack(ArrayList<StickerPack> stickerPackList) {
        progressBar.setVisibility(View.GONE);
        if (stickerPackList != null){
            if (stickerPackList.size() > 1) {
                final Intent intent = new Intent(this, StickerPackListFragment.class);
                intent.putParcelableArrayListExtra(StickerPackListFragment.EXTRA_STICKER_PACK_LIST_DATA, stickerPackList);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            } else if (stickerPackList.size() == 1){
                final Intent intent = new Intent(this, StickerPackDetailsActivity.class);
                intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, false);
                intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPackList.get(0));
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            } else Toast.makeText(this, "No sticker packs", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("StringFormatInvalid")
    private void showErrorMessage(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        Log.e("EntryActivity", "error fetching sticker packs, " + errorMessage);
        final TextView errorMessageTV = findViewById(R.id.error_message);
        errorMessageTV.setText(getString(R.string.error_message, errorMessage));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadListAsyncTask != null && !loadListAsyncTask.isCancelled()) {
            loadListAsyncTask.cancel(true);
        }
    }

    static class LoadListAsyncTask extends AsyncTask<Void, Void, Pair<String, ArrayList<StickerPack>>> {
        private final WeakReference<EntryActivity> contextWeakReference;

        LoadListAsyncTask(EntryActivity activity) {
            this.contextWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Pair<String, ArrayList<StickerPack>> doInBackground(Void... voids) {
            ArrayList<StickerPack> stickerPackList;
            try {
                final Context context = contextWeakReference.get();
                if (context != null) {
                    stickerPackList = StickerPackLoader.fetchStickerPacks(context);
                    if (stickerPackList.size() == 0) {
                        return new Pair<>("could not find any packs", null);
                    }
                    for (StickerPack stickerPack : stickerPackList) {
                        StickerPackValidator.verifyStickerPackValidity(context, stickerPack);
                    }
                    return new Pair<>(null, stickerPackList);
                } else {
                    return new Pair<>("could not fetch sticker packs", null);
                }
            } catch (Exception e) {
                Log.e("EntryActivity", "error fetching sticker packs", e);
                return new Pair<>(e.getMessage(), null);
            }
        }

        @Override
        protected void onPostExecute(Pair<String, ArrayList<StickerPack>> stringListPair) {

            final EntryActivity entryActivity = contextWeakReference.get();
            if (entryActivity != null) {
                if (stringListPair.first != null) {
                    entryActivity.showErrorMessage(stringListPair.first);
                } else {
                    entryActivity.showStickerPack(stringListPair.second);
                }
            }
        }
    }
}
