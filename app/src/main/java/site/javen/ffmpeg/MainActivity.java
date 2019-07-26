package site.javen.ffmpeg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println(FFmpegEncoder.getVersion());
        System.out.println(FFmpegEncoder.listAllCodec());

        new Thread(new Runnable() {
            @Override
            public void run() {
                FFmpegEncoder encoder = new FFmpegEncoder();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timg);
                encoder.init("/sdcard/a.mp3", "/sdcard/e.mp4", bitmap.getWidth(), bitmap.getHeight());

                Log.d(TAG, "onCreate: Begin");
                for (int i = 0; i < 25 * 60; i++) {
                    int ret = encoder.writeFrame(bitmap);
                    Log.d(TAG, "onCreate: " + ret + "   " + i);
                }
                Log.d(TAG, "onCreate: End");
                encoder.free();
            }
        }).start();

    }


    public native void test();

}
