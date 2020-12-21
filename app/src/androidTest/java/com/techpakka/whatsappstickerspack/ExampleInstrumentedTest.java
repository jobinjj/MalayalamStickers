package com.techpakka.whatsappstickerspack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.techpakka.whatsappstickerspack.idoideas.DataArchiver;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.techpakka.whatsappstickerspack", appContext.getPackageName());
    }

    @Test
    public void extractStickers(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String targetLocation = appContext.getFilesDir() + "/stickers/";
        boolean directoryAvailable = dirChecker(targetLocation);

        assertTrue(directoryAvailable);
        try {

            FileInputStream fin = new FileInputStream(appContext.getFilesDir() + "/stickers/stickers.zip");
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                Log.w("DECOMPRESSING FILE", ze.getName());
                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(targetLocation + ze.getName());
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
            Log.d("tag",e.getMessage());
        }

    }

    public class extractZip extends AsyncTask<Void, Void, Void> {
        private String zipPath;
        private Context context;


        public extractZip(Context context, String zipPath) {
            this.context = context;
            this.zipPath = zipPath;
        }


        @Override
        protected Void doInBackground(Void... params) {


            String targetLocation = context.getFilesDir() + "/stickers";
            boolean directoryAvailable = dirChecker(targetLocation);

            assertTrue(directoryAvailable);
            try {

                FileInputStream fin = new FileInputStream(zipPath);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    Log.w("DECOMPRESSING FILE", ze.getName());
                    //create dir if required while unzipping
                    if (ze.isDirectory()) {
                        dirChecker(ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(targetLocation + ze.getName());
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
                Log.d("tag",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void d) {

            Log.w("ENDED DECOMPRESSING", "DONEEEEEE");
        }

    }
    private static boolean dirChecker(String dir) {
        File f = new File(dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
        return f.isDirectory();
    }

    @Test
    public void editPrefs(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = appContext.getSharedPreferences("StickerMaker",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("stickerbook","").apply();
    }
}
