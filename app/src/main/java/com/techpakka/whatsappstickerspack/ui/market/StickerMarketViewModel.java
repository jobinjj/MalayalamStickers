package com.techpakka.whatsappstickerspack.ui.market;

import android.app.Activity;
import android.content.Context;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.ui.HomeActivity;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.Sticker;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StickerMarketViewModel extends ViewModel {


    private WeakReference<Activity> activityWeakReference;
    public Adapter adapter;
    public StickerPackClickListener stickerPackClickListener;


    public StickerMarketViewModel() {
        test();
    }

    private void test() {

    }


    public static class Adapter extends RecyclerView.Adapter<Adapter.StickerPackListItemViewHolder>{

        private List<StickerPack> list;
        private Context appContext;
        private StickerPackClickListener stickerPackClickListener;
        public Adapter(List<StickerPack> list, Context appContext, StickerPackClickListener stickerPackClickListener) {
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
            StickerPack stickerPack = list.get(position);
            holder.title.setText(stickerPack.getName());
            Glide.with(appContext).load(stickerPack.getThumbnail()).into(holder.thumbnail);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stickerPackClickListener.onStickerPackClicked(stickerPack);
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

    public void setStickerPackClickListener(StickerPackClickListener stickerPackClickListener) {
        this.stickerPackClickListener = stickerPackClickListener;
    }

    public void setActivityWeakReference(WeakReference<Activity> activityWeakReference) {
        this.activityWeakReference = activityWeakReference;
        adapter = new Adapter(HomeActivity.serverStickerPacks, activityWeakReference.get(), stickerPackClickListener);
    }

    public interface StickerPackClickListener{
        void onStickerPackClicked(StickerPack stickerPack);
    }
}