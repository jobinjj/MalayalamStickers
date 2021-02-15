package com.techpakka.whatsappstickerspack.room;

import android.os.AsyncTask;

import com.techpakka.whatsappstickerspack.utils.AppController;

import java.util.List;

public class RoomTasks {



    public static class InsertStickerPacksTask extends AsyncTask<Void,Void, Long>{
        StickerPacks stickerPacks;
        InsertStickerPackListener insertStickerPackListener;

        public InsertStickerPacksTask(StickerPacks stickerPacks,InsertStickerPackListener insertStickerPackListener) {
            this.stickerPacks = stickerPacks;
            this.insertStickerPackListener = insertStickerPackListener;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            long stickerPackId = AppController.stickerPacksDao.insert(stickerPacks);
            return stickerPackId;
        }

        @Override
        protected void onPostExecute(Long stickerPackId) {
            super.onPostExecute(stickerPackId);
            stickerPacks.setStickerPackId(stickerPackId);
            insertStickerPackListener.onInserted(stickerPacks);
        }

        public interface InsertStickerPackListener{
            void onInserted(StickerPacks stickerPacks);
        }
    }


    public static class InsertStickersTask extends AsyncTask<Void,Void,Void>{
        Stickers stickers;
        InsertStickerListener insertStickerListener;

        public InsertStickersTask(Stickers stickers,InsertStickerListener insertStickerListener) {
            this.stickers = stickers;
            this.insertStickerListener = insertStickerListener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppController.stickersDao.insert(stickers);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            insertStickerListener.onStickerInserted();
        }

        public interface InsertStickerListener{
            void onStickerInserted();
        }
    }

    public static class FetchStickerPacks extends AsyncTask<Void,Void,List<StickerPacks>>{
        private fetchStickerPacksListener listener;

        public FetchStickerPacks(fetchStickerPacksListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<StickerPacks> doInBackground(Void... voids) {
            return AppController.stickerPacksDao.getAll();
        }

        @Override
        protected void onPostExecute(List<StickerPacks> stickerPacks) {
            super.onPostExecute(stickerPacks);
            listener.onFetched(stickerPacks);
        }

        public interface fetchStickerPacksListener{
            void onFetched(List<StickerPacks> stickerPacks);
        }
    }

    public static class FetchStickers extends AsyncTask<Void,Void,List<Stickers>>{
        private FetchStickersListener listener;

        public FetchStickers(FetchStickersListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<Stickers> doInBackground(Void... voids) {
            return AppController.stickersDao.getAll();
        }

        @Override
        protected void onPostExecute(List<Stickers> stickers) {
            super.onPostExecute(stickers);
            listener.onFetched(stickers);
        }

        public interface FetchStickersListener{
            void onFetched(List<Stickers> stickers);
        }
    }

    public static class FetchStickersById extends AsyncTask<Void,Void,List<Stickers>>{
        private FetchStickersListener listener;
        private long stickerPackId;

        public FetchStickersById(long stickerPackId,FetchStickersListener listener) {
            this.listener = listener;
            this.stickerPackId = stickerPackId;
        }

        @Override
        protected List<Stickers> doInBackground(Void... voids) {
            List<Stickers> stickers = AppController.stickersDao.getAllStickersWithId(stickerPackId);
            return stickers;

        }

        @Override
        protected void onPostExecute(List<Stickers> stickers) {
            super.onPostExecute(stickers);
            listener.onFetched(stickers);
        }

        public interface FetchStickersListener{
            void onFetched(List<Stickers> stickers);
        }
    }

    public static class FetchStickerPackById extends AsyncTask<Void,Void,StickerPacks>{
        private FetchStickerPackListener listener;
        private long stickerPackId;

        public FetchStickerPackById(long stickerPackId,FetchStickerPackListener listener) {
            this.listener = listener;
            this.stickerPackId = stickerPackId;
        }

        @Override
        protected StickerPacks doInBackground(Void... voids) {
            return AppController.stickerPacksDao.getStickerPackWithId(stickerPackId);
        }

        @Override
        protected void onPostExecute(StickerPacks stickerPack) {
            super.onPostExecute(stickerPack);
            listener.onFetched(stickerPack);
        }

        public interface FetchStickerPackListener{
            void onFetched(StickerPacks stickerPack);
        }
    }



    public static class ClearStickerPacks extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            AppController.stickerPacksDao.clearTable();
            return null;
        }
    }
    public static class ClearStickers extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            AppController.stickersDao.clearTable();
            return null;
        }
    }
}
