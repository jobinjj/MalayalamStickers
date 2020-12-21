package com.techpakka.whatsappstickerspack.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.techpakka.whatsappstickerspack.whatsappbasecode.StickerPackListAdapter;
import com.techpakka.whatsappstickerspack.whatsappbasecode.StickerPackLoader;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;

import java.util.List;

public class HomeViewModel extends ViewModel {
    public List<StickerPack> stickerPackList;
    public StickerPackListAdapter.OnAddButtonClickedListener addButtonClickedListener;
    public StickerPackListAdapter adapter;

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setStickerPackList(List<StickerPack> stickerPackList) {
        this.stickerPackList = stickerPackList;
    }

    public void setAddButtonClickedListener(StickerPackListAdapter.OnAddButtonClickedListener addButtonClickedListener) {
        this.addButtonClickedListener = addButtonClickedListener;
    }

    public void setAdapter(StickerPackListAdapter adapter) {
        this.adapter = adapter;
    }
}