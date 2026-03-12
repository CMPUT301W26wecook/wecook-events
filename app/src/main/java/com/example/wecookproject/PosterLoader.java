package com.example.wecookproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PosterLoader {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private PosterLoader() {
    }

    public static void loadInto(ImageView imageView, String posterPath) {
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        imageView.setTag(posterPath);

        if (posterPath == null || posterPath.trim().isEmpty()) {
            return;
        }

        if (!posterPath.startsWith("http://") && !posterPath.startsWith("https://")) {
            return;
        }

        EXECUTOR.execute(() -> {
            try (InputStream inputStream = new URL(posterPath).openStream()) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap == null) {
                    return;
                }
                MAIN_HANDLER.post(() -> {
                    Object tag = imageView.getTag();
                    if (tag instanceof String && posterPath.equals(tag)) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } catch (Exception ignored) {
                // Keep placeholder if loading fails.
            }
        });
    }
}
