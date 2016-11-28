package broadcast.droid2016.ifmo.ru.homework3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String IMAGENAME = "Shashlyk.jpg";
    public static final String ImageDownloadFinished = "ImageDownloadFinished";
    private final String NOTDOWNLOADED = "Не загружено";
    private File cachedImage;
    private ImageView image;
    private TextView errortext;
    private BroadcastReceiver imageLoader;
    private BroadcastReceiver serviceLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.image);
        errortext = (TextView) findViewById(R.id.error_message);

        serviceLauncher = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Service", "Start");
                context.startService(new Intent(context, DownloaderService.class));
            }
        };

        imageLoader = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Image", "Trying to show");
                showImage();
            }
        };

        registerReceivers();
    }

    boolean tryToOpenFile() {
        cachedImage = new File(getFilesDir(), IMAGENAME);
        return cachedImage.exists();
    }

    void showImage() {
        if (tryToOpenFile()) {
            image.setImageBitmap(BitmapFactory.decodeFile(cachedImage.getAbsolutePath()));
            image.setVisibility(View.VISIBLE);
            errortext.setVisibility(View.INVISIBLE);
        } else {

            errortext.setText("Не загружено");
            image.setVisibility(View.INVISIBLE);
            errortext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }

    private void registerReceivers() {
        registerReceiver(serviceLauncher, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(imageLoader, new IntentFilter(ImageDownloadFinished));
    }

    private void unregisterReceivers() {
        unregisterReceiver(serviceLauncher);
        unregisterReceiver(imageLoader);
    }

}
