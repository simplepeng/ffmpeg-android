package site.javen.ffmpeg;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.airbnb.lottie.FontAssetDelegate;
import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(FFmpegEncoder.getVersion());
        System.out.println(FFmpegEncoder.listAllCodec());

        final ImageView imageView = findViewById(R.id.imageView);

        final LottieDrawable drawable = new LottieDrawable();
        drawable.setImageAssetDelegate(new ImageAssetDelegate() {
            @Nullable
            @Override
            public Bitmap fetchBitmap(LottieImageAsset asset) {
                String name = asset.getId().replace("image_", "img_");
                try {
                    return BitmapFactory.decodeStream(getAssets().open(name + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            }
        });
        drawable.setFontAssetDelegate(new FontAssetDelegate() {
            @Override
            public Typeface fetchFont(String fontFamily) {
                return Typeface.createFromAsset(getAssets(), "Source Han Sans CN.ttf");
            }
        });
        drawable.setCallback(imageView);
        final int density = (int) getResources().getDisplayMetrics().density;
        LottieTask<LottieComposition> task = LottieCompositionFactory.fromAsset(this, "data.json");
        task.addListener(new LottieListener<LottieComposition>() {
            @Override
            public void onResult(final LottieComposition result) {
                drawable.setComposition(result);

                Rect bounds = result.getBounds();
                final int frameW = (int) ((bounds.right - bounds.left));
                final int frameH = (int) ((bounds.bottom - bounds.top));

                final int w = (int) (frameW / density);
                final int h = (int) (frameH / density);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        float start = System.currentTimeMillis();
                        FFmpegEncoder encoder = new FFmpegEncoder();
                        int max = (int) drawable.getMaxFrame();
                        int min = (int) drawable.getMinFrame();
                        encoder.init("/sdcard/a.mp3", "/sdcard/e.mp4", w, h);

                        for (int i = 0; i < max - min; i++) {
                            drawable.setFrame(i);
//                           final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_0);
                            final Bitmap bitmap = parseBitmap(drawable, frameW, frameH, density);
                            int ret = encoder.writeFrame(bitmap);
//                            Log.d(TAG, "frame == " + i);
                            Log.d(TAG, "ret == " + ret);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                }
                            });

                        }

                        Log.d(TAG, "onCreate: End");
                        encoder.free();
                        float end = System.currentTimeMillis();
                        Log.d(TAG, "time == " + (end - start) / 1000);
                    }
                }).start();
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                FFmpegEncoder encoder = new FFmpegEncoder();
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timg);
//
//                encoder.init("/sdcard/a.mp3", "/sdcard/e.mp4", bitmap.getWidth(), bitmap.getHeight());
//
//                Log.d(TAG, "onCreate: Begin");
//                for (int i = 0; i < 25 * 10; i++) {
//                    int ret = encoder.writeFrame(bitmap);
//                    Log.d(TAG, "onCreate: " + ret + "   " + i);
//                }
//                Log.d(TAG, "onCreate: End");
//                encoder.free();
//            }
//        }).start();

    }

    private Bitmap parseBitmap(LottieDrawable drawable, int frameW, int frameH, float density) {
        int w = (int) (frameW / density);
        int h = (int) (frameH / density);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(bitmap);
        final float scale = 1 / density;
        bitmapCanvas.scale(scale, scale);
        drawable.draw(bitmapCanvas);
        return bitmap;
    }

    public native void test();

}
