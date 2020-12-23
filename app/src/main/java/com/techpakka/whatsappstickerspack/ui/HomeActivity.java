package com.techpakka.whatsappstickerspack.ui;

import android.os.AsyncTask;
import android.os.Bundle;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mallucoder.myutils.inappupdates.InAppUpdates;
import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.idoideas.DataArchiver;
import com.techpakka.whatsappstickerspack.idoideas.StickerBook;
import com.techpakka.whatsappstickerspack.whatsappbasecode.AddStickerPackActivity;
import com.techpakka.whatsappstickerspack.whatsappbasecode.StickerPackListAdapter;
import com.techpakka.whatsappstickerspack.whatsappbasecode.WhitelistCheck;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.Sticker;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AddStickerPackActivity implements StickerPackListAdapter.OnAddButtonClickedListener {

    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;

    public static List<StickerPack> localStickerPackList;
    public static StickerPackListAdapter.OnAddButtonClickedListener addButtonClickedListener;
    public static StickerPackListAdapter adapter;

    public static ArrayList<StickerPack> serverStickerPacks = new ArrayList<>();
    public static MutableLiveData<ArrayList<StickerPack>> mutableStickerPacks = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
/*        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_home, R.id.navigation_notifications)
                .build();*/
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_market, R.id.navigation_downloads)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        InAppUpdates.checkForUpdates(this);

        Fresco.initialize(this);

        addButtonClickedListener = this;
        getStickersFromMarket();
    }

    @Override
    public void onAddButtonClicked(StickerPack stickerPack) {

        addStickerPackToWhatsApp(stickerPack.identifier, stickerPack.name);
    }

    static class WhiteListCheckAsyncTask extends AsyncTask<List<StickerPack>, Void, List<StickerPack>> {
        private final WeakReference<HomeActivity> homeActivityWeakReference;

        WhiteListCheckAsyncTask(HomeActivity homeActivity) {
            this.homeActivityWeakReference = new WeakReference<>(homeActivity);
        }

        @SafeVarargs
        @Override
        protected final List<StickerPack> doInBackground(List<StickerPack>... lists) {
            List<StickerPack> stickerPackList = lists[0];
            final HomeActivity stickerPackListActivity = homeActivityWeakReference.get();
            if (stickerPackListActivity == null) {
                return stickerPackList;
            }
            for (StickerPack stickerPack : stickerPackList) {
                stickerPack.setIsWhitelisted(WhitelistCheck.isWhitelisted(stickerPackListActivity, stickerPack.identifier));
            }
            return stickerPackList;
        }

        @Override
        protected void onPostExecute(List<StickerPack> stickerPackList) {
            final HomeActivity homeActivity = homeActivityWeakReference.get();
            if (homeActivity != null) {
                // homeActivity.allStickerPacksListAdapter.notifyDataSetChanged();
            }
            if (HomeActivity.adapter != null){
                HomeActivity.adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


        if (HomeActivity.localStickerPackList != null){

            whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
            //noinspection unchecked
            whiteListCheckAsyncTask.execute(HomeActivity.localStickerPackList);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        DataArchiver.writeStickerBookJSON(StickerBook.getAllStickerPacks(), this);
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        DataArchiver.writeStickerBookJSON(StickerBook.getAllStickerPacks(), this);
        super.onDestroy();
    }

    public static void setLocalStickerPackList(List<StickerPack> localStickerPackList) {
        HomeActivity.localStickerPackList = localStickerPackList;
    }


    public static void setAdapter(StickerPackListAdapter adapter) {
        HomeActivity.adapter = adapter;
    }

    private static void getStickersFromMarket() {
        if (serverStickerPacks.isEmpty()){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Stickerpacks").orderBy("time_stamp", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    StickerPack stickerPack = new StickerPack();
                                    stickerPack.setName(document.getString("name"));
                                    stickerPack.setIdentifier(document.getString("identifier"));
                                    stickerPack.setTrayImageFile(document.getString("trayImageFile"));
                                    stickerPack.setThumbnail(document.getString("thumbnail"));
                                    stickerPack.setDownloadUrl(document.getString("downloadUrl"));
                                    getStickers(stickerPack,document);
                                }
                            } else {
                            }
                        }

                        private void getStickers(StickerPack stickerPack, QueryDocumentSnapshot document) {
                            db.collection("Stickerpacks").document(document.getString("name")).collection("stickers")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                ArrayList<Sticker> stickerlist = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Sticker sticker = new Sticker();
                                                    sticker.setSize((long) Double.parseDouble(document.getString("size")));
                                                    sticker.setImageFileName(document.getString("imageFileName"));
                                                    sticker.setEmojis((List<String>) document.get("emojis"));
                                                    sticker.setStickerUrl(document.getString("stickerUrl"));
                                                    stickerlist.add(sticker);
                                                }
                                                stickerPack.setStickers(stickerlist);
                                                serverStickerPacks.add(stickerPack);
                                                mutableStickerPacks.setValue(serverStickerPacks);
                                            } else {
                                            }
                                        }
                                    });
                        }
                    });
        }


    }
}