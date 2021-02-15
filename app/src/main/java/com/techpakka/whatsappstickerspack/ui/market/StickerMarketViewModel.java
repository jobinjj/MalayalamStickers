package com.techpakka.whatsappstickerspack.ui.market;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.room.RoomTasks;
import com.techpakka.whatsappstickerspack.room.StickerPacks;
import com.techpakka.whatsappstickerspack.room.Stickers;
import com.techpakka.whatsappstickerspack.ui.HomeActivity;
import com.techpakka.whatsappstickerspack.utils.Constants;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.Sticker;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StickerMarketViewModel extends ViewModel {


    private static final String TAG = StickerMarketViewModel.class.getSimpleName();
    private WeakReference<Activity> activityWeakReference;
    public Adapter adapter;
    public MutableLiveData<Integer> progress = new MutableLiveData<>();
    public StickerPackClickListener stickerPackClickListener;


    public StickerMarketViewModel() {
    }


    public void setStickerPackClickListener(StickerPackClickListener stickerPackClickListener) {
        this.stickerPackClickListener = stickerPackClickListener;
    }

    public void init(WeakReference<Activity> activityWeakReference) {
        this.activityWeakReference = activityWeakReference;
        adapter = new Adapter(HomeActivity.serverStickerPacks, activityWeakReference.get(), stickerPackClickListener);
        getStickersFromMarket();
    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.StickerPackListItemViewHolder>{

        private List<StickerPacks> list;
        private Context appContext;
        private StickerPackClickListener stickerPackClickListener;
        public Adapter(List<StickerPacks> list, Context appContext, StickerPackClickListener stickerPackClickListener) {
            this.list = list;
            this.appContext = appContext;
            this.stickerPackClickListener = stickerPackClickListener;
        }

        @NonNull
        @Override
        public StickerPackListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final Context context = parent.getContext();
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            final View stickerPackRow = layoutInflater.inflate(R.layout.market_sticker_packs_list_item, parent, false);
            return new StickerPackListItemViewHolder(stickerPackRow);
        }

        @Override
        public void onBindViewHolder(@NonNull StickerPackListItemViewHolder holder, int position) {
            StickerPacks stickerPacks = list.get(position);
            holder.title.setText(stickerPacks.getName());
            Glide.with(appContext).load(stickerPacks.getThumbnail()).into(holder.thumbnail);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stickerPackClickListener.onStickerPackClicked(stickerPacks);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class StickerPackListItemViewHolder extends RecyclerView.ViewHolder {

            public CardView container;
            public TextView title;
            public ImageView thumbnail;
            public StickerPackListItemViewHolder(final View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.name);
                thumbnail = itemView.findViewById(R.id.thumbnail);
                container = itemView.findViewById(R.id.container);
            }
        }
    }
    public interface StickerPackClickListener{
        void onStickerPackClicked(StickerPacks stickerPacks);
    }

    private void getStickersFromMarket() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (HomeActivity.serverStickerPacks.isEmpty()){
            if (Constants.isNetworkConnected(activityWeakReference.get())){
                getStickerPacks(db);
            }else {
                getStickerPacksFromRoom();
            }
        }else progress.setValue(View.GONE);
    }

    private void getStickerPacksFromRoom() {
        new RoomTasks.FetchStickerPacks(new RoomTasks.FetchStickerPacks.fetchStickerPacksListener() {
            @Override
            public void onFetched(List<StickerPacks> stickerPacks) {
                progress.setValue(View.GONE);
                HomeActivity.serverStickerPacks.addAll(stickerPacks);
            }
        }).execute();
    }


    private void getStickerPacks(FirebaseFirestore db) {
        db.collection("Stickerpacks").orderBy("time_stamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            new RoomTasks.ClearStickerPacks().execute();
                            new RoomTasks.ClearStickers().execute();
                            List<DocumentSnapshot> list = task.getResult().getDocuments();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                StickerPacks stickerPack = new StickerPacks();
                                stickerPack.setName(document.getString("name"));
                                stickerPack.setIdentifier(document.getString("identifier"));
                                stickerPack.setTrayImageFile(document.getString("trayImageFile"));
                                stickerPack.setThumbnail(document.getString("thumbnail"));
                                stickerPack.setDownloadUrl(document.getString("downloadUrl"));
                                new RoomTasks.InsertStickerPacksTask(stickerPack, new RoomTasks.InsertStickerPacksTask.InsertStickerPackListener() {
                                    @Override
                                    public void onInserted(StickerPacks stickerPacks) {
                                        getStickers(db,stickerPacks.getStickerPackId(),document.getString("name"));
                                        if (adapter != null){
                                            HomeActivity.serverStickerPacks.add(stickerPacks);
                                            adapter.notifyDataSetChanged();
                                            progress.setValue(View.GONE);
                                        }
                                    }
                                }).execute();
                            }
                        } else {

                        }
                    }

                });
    }

        private void getStickers(FirebaseFirestore db, long stickerPackId, String documentname) {
            db.collection("Stickers").whereEqualTo("stickerPack",documentname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                List<DocumentSnapshot> list = task.getResult().getDocuments();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("workplease", document.getId() + " => " + document.getData());

                                    Stickers sticker = new Stickers();
                                    sticker.setSize((long) Double.parseDouble(document.getString("size")));
                                    sticker.setImageFileName(document.getString("imageFileName"));
                                    sticker.setStickerUrl(document.getString("stickerUrl"));
                                                    /*String emojis = document.getString("emojis");
                                                    sticker.setEmojis(emojis);*/
                                    sticker.setStickerPackId(stickerPackId);
                                    new RoomTasks.InsertStickersTask(sticker, new RoomTasks.InsertStickersTask.InsertStickerListener() {
                                        @Override
                                        public void onStickerInserted() {

                                        }
                                    }).execute();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                            }


                        } else {
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,e.getMessage());
                }
            });
    }
}