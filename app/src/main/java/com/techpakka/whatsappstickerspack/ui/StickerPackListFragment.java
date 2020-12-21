package com.techpakka.whatsappstickerspack.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.airbnb.lottie.LottieAnimationView;
import com.techpakka.whatsappstickerspack.R;
import com.techpakka.whatsappstickerspack.idoideas.StickerBook;
import com.techpakka.whatsappstickerspack.ui.home.HomeViewModel;
import com.techpakka.whatsappstickerspack.whatsappbasecode.StickerPackListAdapter;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import java.util.ArrayList;
import java.util.List;


public class StickerPackListFragment extends Fragment {
    public static final String EXTRA_STICKER_PACK_LIST_DATA = "sticker_pack_list";
    private static final int STICKER_PREVIEW_DISPLAY_LIMIT = 5;
    private LinearLayoutManager packLayoutManager;
    private RecyclerView packRecyclerView;
    private StickerPackListAdapter allStickerPacksListAdapter;
    private ArrayList<StickerPack> stickerPackList;
    private boolean firstTime = true;


    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sticker_pack_list, container, false);
        ConstraintLayout noStickersFound = root.findViewById(R.id.noStickersContainer);
  /*      final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        packRecyclerView = root.findViewById(R.id.sticker_pack_list);

        if (hasDownloadedStickers()){
            StickerBook.init(getActivity());
            stickerPackList = StickerBook.getAllStickerPacks();//getIntent().getParcelableArrayListExtra( EXTRA_STICKER_PACK_LIST_DATA);
            showStickerPackList(stickerPackList);
        }else noStickersFound.setVisibility(View.VISIBLE);

        return root;
    }

    private boolean hasDownloadedStickers() {
        return getActivity().getSharedPreferences("StickerMaker", Context.MODE_PRIVATE).getString("stickerbook","").length() > 3 ;
    }

    public void showStickerPackList(List<StickerPack> stickerPackList) {
        HomeActivity.setLocalStickerPackList(stickerPackList);
        allStickerPacksListAdapter = new StickerPackListAdapter(stickerPackList, HomeActivity.addButtonClickedListener);
        packRecyclerView.setAdapter(allStickerPacksListAdapter);
        HomeActivity.setAdapter(allStickerPacksListAdapter);
        packLayoutManager = new LinearLayoutManager(getActivity());
        packLayoutManager.setOrientation(RecyclerView.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                packRecyclerView.getContext(),
                packLayoutManager.getOrientation()
        );
        packRecyclerView.addItemDecoration(dividerItemDecoration);
        packRecyclerView.setLayoutManager(packLayoutManager);
        allStickerPacksListAdapter.notifyDataSetChanged();
       // packRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this::recalculateColumnCount);
    }


/*    private void recalculateColumnCount() {
        final int previewSize = getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size);
        int firstVisibleItemPosition = packLayoutManager.findFirstVisibleItemPosition();
        StickerPackListItemViewHolder viewHolder = (StickerPackListItemViewHolder) packRecyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition);
        if (viewHolder != null) {
            final int widthOfImageRow = viewHolder.imageRowView.getMeasuredWidth();
            final int max = Math.max(widthOfImageRow / previewSize, 1);
            int maxNumberOfImagesInARow = Math.min(STICKER_PREVIEW_DISPLAY_LIMIT, max);
            int minMarginBetweenImages = (widthOfImageRow - maxNumberOfImagesInARow * previewSize) / (maxNumberOfImagesInARow - 1);
            allStickerPacksListAdapter.setImageRowSpec(maxNumberOfImagesInARow, minMarginBetweenImages);
        }
    }*/

    public void addNewStickerPack(View view) {

    }


}
