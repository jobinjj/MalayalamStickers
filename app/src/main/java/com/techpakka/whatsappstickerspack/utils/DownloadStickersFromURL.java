package com.techpakka.whatsappstickerspack.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.DownloadListener;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadStickersFromURL extends AsyncTask<String, String, String> {

    private String mFileSize;
    private StickersDownloadListener downloadListener;
    private URL url;
    private Context context;

    public DownloadStickersFromURL(String mFileSize, StickersDownloadListener downloadListener, URL url, Context context) {
        this.mFileSize = mFileSize;
        this.downloadListener = downloadListener;
        this.url = url;
        this.context = context;
    }

    /**
     * Before starting background thread Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        downloadListener.onDownloadStarted();
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {

            URLConnection connection = url.openConnection();
            connection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = Integer.parseInt(mFileSize);;


            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream

            File parentFolder = new File(context.getFilesDir() + File.separator + "stickers");
            if (!parentFolder.exists()){
                parentFolder.mkdirs();
            }
            OutputStream output = new FileOutputStream(parentFolder+ File.separator + "stickers.zip");


            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        }
        catch (InterruptedIOException e) {
            Log.e("Error: ", e.getMessage());
            return "failed";
        }
       catch (Exception e) {
            Log.e("Error: ", e.getMessage());
           downloadListener.onFailed(url.toString());
           return "failed";
        }

        return "success";
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        downloadListener.onDownloadProgress(Integer.parseInt(progress[0]));

    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String response) {
        // dismiss the dialog after the file was downloaded

        if (response.equals("success"))
            downloadListener.onDownloadCompleted();


    }

    public void downloadCancelled(){
        downloadListener.onCancelled();
       /* if (mFilePath != null){
            File fdelete = new File(mFilePath);
            if (fdelete.exists()) {
                fdelete.delete();
            }
        }*/

    }
    public interface StickersDownloadListener{
        void onDownloadStarted();
        void onFailed(String error);
        void onDownloadProgress(int progress);
        void onDownloadCompleted();
        void onCancelled();
    }
}
