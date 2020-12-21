package com.techpakka.whatsappstickerspack.idoideas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.Sticker;
import com.techpakka.whatsappstickerspack.whatsappbasecode.models.StickerPack;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DataArchiver {

    private static int BUFFER = 8192;

    public static boolean writeStickerBookJSON(List<StickerPack> sb, Context context)
    {
        try {
            SharedPreferences mSettings = context.getSharedPreferences("StickerMaker", Context.MODE_PRIVATE);

            String writeValue = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new UriSerializer())
                    .create()
                    .toJson(
                    sb,
                    new TypeToken<ArrayList<StickerPack>>() {}.getType());
            SharedPreferences.Editor mEditor = mSettings.edit();
            Log.d("stickerjson", String.valueOf(writeValue));
            mEditor.putString("stickerbook", writeValue);
            mEditor.apply();
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    } public static boolean writeToSharedPrefs(String json, Context context)
    {
        try {
            SharedPreferences mSettings = context.getSharedPreferences("StickerMaker", Context.MODE_PRIVATE);

            SharedPreferences.Editor mEditor = mSettings.edit();
            Log.d("stickerjson", String.valueOf(json));
            mEditor.putString("stickerbook", json);
            mEditor.apply();
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public static ArrayList<StickerPack> readStickerPackJSONFromSharedPref(Context context)
    {
        SharedPreferences mSettings = context.getSharedPreferences("StickerMaker", Context.MODE_PRIVATE);

        String loadValue = mSettings.getString("stickerbook", "");
        Log.d("stickerjson",loadValue);
        Type listType = new TypeToken<ArrayList<StickerPack>>(){}.getType();
        return new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .create()
                .fromJson(loadValue, listType);
    }

    public static ArrayList<StickerPack> readStickerPackJSONFromFile(Context context)
    {
        File content = new File(context.getFilesDir() + File.separator + "stickers/contents.json");

        try {
            InputStream inputStream = new FileInputStream(content);
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));

            Type listType = new TypeToken<ArrayList<StickerPack>>(){}.getType();
            return new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new UriDeserializer())
                    .create()
                    .fromJson(reader, listType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String createZipFileFromStickerPack(StickerPack sp, Context context){
        String id = sp.getIdentifier();
        String path = context.getFilesDir()+"/"+id;

        createZip cz = new createZip(context, sp);
        cz.execute();

        return path + "/" + sp.getIdentifier() + ".zip";
    }

    public static void importZipFileToStickerPack(Uri uri, Context context){
        String zipPath = "";
        try {
            zipPath = FilesUtils.inputStreamToSavedFile(context.getContentResolver().openInputStream(uri), context, UUID.randomUUID().toString()+".zip");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        parseZip pz = new parseZip(context, zipPath);
        pz.execute();

    }

    public static void zip(ArrayList<String> _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
                Log.v("Compress", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dirChecker(String dir) {
        File f = new File(dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    private static void stickerPackToJSONFile(StickerPack sp, String path, Context context) {
        try {
            String writeValue = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new UriSerializer())
                    .create()
                    .toJson(
                            sp,
                            StickerPack.class);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(path, sp.getIdentifier()+".json")));
            outputStreamWriter.write(writeValue);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static StickerPack JSONFileToStickerPack(String id, String path, Context context) {

        String ret = "";

        try {

            InputStream inputStream = new FileInputStream(new File(path, id+".json"));


            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .create()
                .fromJson(ret, StickerPack.class);
    }


    public static void unzip(String _zipFile, String _targetLocation) {

        //create target location folder if not exist
        dirChecker(_targetLocation);

        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.w("DECOMPRESSING FILE", ze.getName());
                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
            Log.w("ENDED DECOMPRESSING", "DONEEEEEE");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static class UriSerializer implements JsonSerializer<Uri> {
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class UriDeserializer implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(final JsonElement src, final Type srcType,
                               final JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.toString().replace("\"", ""));
        }
    }

    private static class createZip extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private StickerPack sp;

        public createZip(Context context, StickerPack sp) {
            dialog = new ProgressDialog(context);
            this.sp = sp;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Creating Sticker Pack Zip, Please wait...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void d) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            String id = sp.getIdentifier();
            String path = dialog.getContext().getFilesDir()+"/"+id;
            ExternalPacksManager.sendStickerPackZipThroughWhatsApp(dialog.getContext(), id);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String id = sp.getIdentifier();
            ArrayList<String> namesOfFiles = new ArrayList<>();
            String path = dialog.getContext().getFilesDir()+"/"+id;

            stickerPackToJSONFile(sp, path+"/", dialog.getContext());

            for(Sticker s : sp.getStickers()){
                namesOfFiles.add(path+"/"+sp.getIdentifier()+"-"+s.getImageFileName()+".webp");
            }
            namesOfFiles.add(path+"/"+sp.getIdentifier()+"-trayImage.webp");
            namesOfFiles.add(path+"/"+sp.getIdentifier()+".json");
            zip(namesOfFiles, path + "/" + sp.getIdentifier() + ".zip");
            return null;
        }

    }

    private static class parseZip extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String zipPath;
        private boolean isAlreadyLoaded = false;
        private boolean isFailed = false;


        public parseZip(Context context, String zipPath) {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            this.zipPath = zipPath;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Parsing Sticker Pack Zip, Please Wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void d) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (isAlreadyLoaded){
                AlertDialog.Builder builder = new AlertDialog.Builder(dialog.getContext());
                builder.setMessage("Sticker Pack is already loaded.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            if (isFailed){
                AlertDialog.Builder builder = new AlertDialog.Builder(dialog.getContext());
                builder.setMessage("The zip file that was imported is not a proper sticker pack file.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String packId = zipPath.split("/")[zipPath.split("/").length-1].replace(".zip", "");
            unzip(zipPath, dialog.getContext().getFilesDir()+"/"+packId+"/");

            String path = dialog.getContext().getFilesDir()+"/"+packId;

            String originalID = FilesUtils.getActualIDOfPack(path+"/");

            File oldFolder = new File(dialog.getContext().getFilesDir(),packId);

            if(StickerBook.getStickerPackById(originalID)==null){
                File newFolder = null;
                try{
                    newFolder = new File(dialog.getContext().getFilesDir(),originalID);
                    oldFolder.renameTo(newFolder);

                    StickerPack sp = JSONFileToStickerPack(originalID, newFolder.getAbsolutePath(), dialog.getContext());

                    StickerBook.addStickerPackExisting(sp);
                } catch (Exception e){
                    isFailed = true;
                    if(newFolder!=null){
                        newFolder.delete();
                    } else {
                        oldFolder.delete();
                    }

                }

            } else {
                oldFolder.delete();
                isAlreadyLoaded = true;
            }
            return null;
        }

    }


    public static class extractZip extends AsyncTask<Void, Void, Void> {
        private String outputLocation;
        private String zipPath;
        private Context context;
        private zipListener listener;
        private int totalFiles = 328;


        public extractZip(Context context, String outputLocation, String zipPath, zipListener listener) {
            this.context = context;
            this.outputLocation = outputLocation;
            this.zipPath = zipPath;
            this.listener = listener;
        }


        @Override
        protected Void doInBackground(Void... params) {

            int extracted = 0;
            try {
                dirChecker(context.getFilesDir() + "/stickers/");
                FileInputStream fin = new FileInputStream(zipPath);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze;
                while ((ze = zin.getNextEntry()) != null) {
                    Log.w("DECOMPRESSING FILE", ze.getName());
                    extracted++;
                    listener.onExtractProgress((int) ((extracted * 100) / totalFiles));
                    //create dir if required while unzipping
                    if (ze.isDirectory()) {
                        dirChecker(ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(outputLocation + ze.getName());
                        BufferedOutputStream bufout = new BufferedOutputStream(fout);
                        byte[] buffer = new byte[1024];
                        int read = 0;
                        while ((read = zin.read(buffer)) != -1) {
                            bufout.write(buffer, 0, read);
                        }

                        zin.closeEntry();
                        bufout.close();
                        fout.close();
                    }

                }
                zin.close();

            } catch (Exception e) {
                Log.d("tag",e.getMessage());
                listener.onExtractionFailed();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void d) {

            Log.w("ENDED DECOMPRESSING", "DONEEEEEE");
            listener.onExtracted();
        }


    }
    public interface zipListener {
        void onExtracted();
        void onExtractionFailed();
        void onExtractProgress(int progress);
    }
}
