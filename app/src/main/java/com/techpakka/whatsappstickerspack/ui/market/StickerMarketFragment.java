package com.techpakka.whatsappstickerspack.ui.market;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.ui.HomeActivity;
import com.techpakka.whatsappstickerspack.ui.notifications.StickerPackDownloadActivity;
import com.techpakka.whatsappstickerspack.whatsappbasecode.BottomFadingRecyclerView;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class StickerMarketFragment extends Fragment implements StickerMarketViewModel.StickerPackClickListener {

    private StickerMarketViewModel stickerMarketViewModel;
    private final String TAG = StickerMarketFragment.class.getSimpleName();
    ArrayList<StickerPack> stickerPacks = new ArrayList<>();
    private BottomFadingRecyclerView stickerRecyclerView;
    private ArrayList<StickerPack> stickerPacklist = new ArrayList<>();
    private ProgressBar progressBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        stickerMarketViewModel =
                ViewModelProviders.of(this).get(StickerMarketViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sticker_market, container, false);
        stickerRecyclerView = root.findViewById(R.id.market_sticker_list);
        progressBar = root.findViewById(R.id.progressBar);

        stickerMarketViewModel.setStickerPackClickListener(this);
        stickerMarketViewModel.setActivityWeakReference(new WeakReference<>(getActivity()));

        HomeActivity.mutableStickerPacks.observe(getViewLifecycleOwner(), new Observer<ArrayList<StickerPack>>() {
            @Override
            public void onChanged(ArrayList<StickerPack> stickerPacks) {
                progressBar.setVisibility(View.GONE);
                stickerMarketViewModel.adapter.notifyDataSetChanged();
            }
        });

        setUpList(stickerRecyclerView, stickerMarketViewModel.adapter);

        return root;
    }

    private void setUpList(RecyclerView stickerRecyclerView, StickerMarketViewModel.Adapter adapter) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),gridLayoutManager.getOrientation());
        //stickerRecyclerView.addItemDecoration(dividerItemDecoration);
        stickerRecyclerView.setLayoutManager(gridLayoutManager);
        stickerRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStickerPackClicked(StickerPack stickerPack) {
        Intent intent = new Intent(getActivity(), StickerPackDownloadActivity.class);
        intent.putExtra("stickerPack",stickerPack);
        getActivity().startActivity(intent);
    }




}