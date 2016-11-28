package broadcast.droid2016.ifmo.ru.homework3;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mekhrubon on 28.11.16.
 */

public class DownloaderService extends Service {

    boolean loadStarted = false;
    private String link = "http://design.sha-sha.ru/meat.jpg";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "Started");
        if (loadStarted) return START_STICKY;

        loadStarted = true;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Log.d("Thread", "Downloading");
                File f = new File(getFilesDir(), MainActivity.IMAGENAME);
                if (!f.exists() || BitmapFactory.decodeFile(f.getAbsolutePath()) == null) {

                    HttpURLConnection con = null;
                    InputStream in = null;
                    FileOutputStream out = null;


                    try {
                        con = (HttpURLConnection) new URL(link).openConnection();
                        in = new BufferedInputStream(con.getInputStream());
                        out = new FileOutputStream(f);
                        byte[] buf = new byte[10000];
                        int bufLength;
                        while ((bufLength = in.read(buf)) != -1) {
                            out.write(buf, 0, bufLength);
                        }
                        Log.d("Download", "finished\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (con != null) con.disconnect();
                            if (out != null) out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d("Broadcast", "sent");
                sendBroadcast(new Intent(MainActivity.ImageDownloadFinished));
                loadStarted = false;
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
