package com.theone.sns.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.theone.sns.R;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageLoaderUtil {

    private static final String TAG = "ImageLoaderUtil";

    public static final int LOAD_ONE_THUMBNAIL_DONE = 1;

    /*
     * The simplest in-memory cache implementation. This should be replaced with
     * something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
     */
    private static final HashMap<String, SoftReference<Bitmap>> CACHE = new HashMap<String, SoftReference<Bitmap>>();

    private static final ArrayList<String> LOADING = new ArrayList<String>();
    /**
     * 线程池最大线程数:5
     */
    private static final int THREAD_POOL_MAX_SIZE = 5;
    /**
     * 线程池最大线程数:5
     */
    private static Executor sFixedThreadPoolExecutor = Executors
            .newFixedThreadPool(THREAD_POOL_MAX_SIZE);
    private static final DisplayImageOptions optionsForUserIconRounded = new DisplayImageOptions.Builder()
            .showImageOnLoading(new ColorDrawable(0xfff0f0f0))
            .showImageForEmptyUri(R.drawable.home_user_icon)
            .showImageOnFail(R.drawable.home_user_icon).cacheInMemory(true).cacheOnDisk(true)
            .considerExifParams(true).displayer(new RoundedBitmapDisplayer(10)).build();
    private Context mContext;
    private ArrayList<String> mPaths;

    private ArrayList<Integer> mIds;

    public ImageLoaderUtil(Context context, ArrayList<String> paths, ArrayList<Integer> ids) {
        mContext = context;
        mPaths = paths;
        mIds = ids;
    }

    /**
     * 根据宽度从本地图片路径获取该图片的缩略图
     *
     * @param localImagePath 本地图片的路径
     * @param reqWidth       缩略图的宽
     * @param addedScaling   额外可以加的缩放比例
     * @return bitmap 指定宽高的缩略图
     */
    public static Bitmap getBitmapByWidth(String localImagePath, int reqWidth, int addedScaling,
                                          int reqHeight) {
        if (TextUtils.isEmpty(localImagePath)) {
            return null;
        }

        Bitmap temBitmap = null;
        try {
            Options outOptions = new Options();
            // 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
            outOptions.inJustDecodeBounds = true;

            // 加载获取图片的宽高
            BitmapFactory.decodeFile(localImagePath, outOptions);
            // int height = outOptions.outHeight;

            // 源图片的高度和宽度
            final int height = outOptions.outHeight;
            final int width = outOptions.outWidth;
            int sample;
            outOptions.inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                // 计算出实际宽高和目标宽高的比率
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
                // 一定都会大于等于目标的宽和高。
                sample = heightRatio < widthRatio ? heightRatio : widthRatio;
                sample = sample + 1;
                if (sample < 3)
                    outOptions.inSampleSize = (int) sample;
                else if (sample < 6.5)
                    outOptions.inSampleSize = 4;
                else if (sample < 8)
                    outOptions.inSampleSize = 8;
                else
                    outOptions.inSampleSize = (int) sample;
            }
            // 重新设置该属性为false，加载图片返回
            outOptions.inJustDecodeBounds = false;
            outOptions.inPurgeable = true;
            outOptions.inInputShareable = true;
            outOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return temBitmap;
    }

    public static boolean isGIF(String path) {
        int lastDot = path.lastIndexOf(".");
        if (lastDot < 0) {
            return false;
        }

        String suffix = path.substring(lastDot + 1).toUpperCase();
        return "GIF".equals(suffix);
    }

    /**
     * 根据宽度从本地图片路径获取该图片的缩略图
     *
     * @param localImagePath 本地图片的路径
     * @return bitmap 指定宽高的缩略图
     */
    public static Bitmap getBitmapFormPath(String localImagePath) {
        if (TextUtils.isEmpty(localImagePath)) {
            return null;
        }

        Bitmap temBitmap = null;
        try {
            temBitmap = BitmapFactory.decodeFile(localImagePath);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return temBitmap;
    }

    /**
     */
    public static Bitmap decodeResource(Resources res, int id) {
        return BitmapFactory.decodeResource(res, id);
    }

    /**
     * 根据宽度从本地图片路径获取该图片
     *
     * @param localImagePath 本地图片的路径
     * @param width          缩略图的宽
     * @param addedScaling   额外可以加的缩放比例
     * @return bitmap 指定宽高的缩略图
     */
    public static Bitmap getBigBitmapByWidth(String localImagePath, int width, int addedScaling) {
        if (TextUtils.isEmpty(localImagePath)) {
            return null;
        }
        Bitmap temBitmap = null;
        try {
            Options outOptions = new Options();
            // 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
            outOptions.inJustDecodeBounds = true;

            // 加载获取图片的宽高
            BitmapFactory.decodeFile(localImagePath, outOptions);
            int height = outOptions.outHeight;

            if (outOptions.outWidth > width) {
                // 根据宽设置缩放比例
                outOptions.inSampleSize = outOptions.outWidth / width + 1 + addedScaling;
                outOptions.outWidth = width;

                // 计算缩放后的高度
                height = outOptions.outHeight / outOptions.inSampleSize;
                outOptions.outHeight = height;
            }

            // 重新设置该属性为false，加载图片返回
            outOptions.inJustDecodeBounds = false;
            outOptions.inPurgeable = true;
            outOptions.inInputShareable = true;
            outOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return temBitmap;
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fd, Rect rect, Options options) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFileDescriptor(fd, rect, options);
        return bitmap;
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(data, offset, length, opts);
        return bitmap;
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(data, offset, length);
        return bitmap;
    }

    public static Bitmap decodeFile(String pathName) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(pathName);

        return bitmap;
    }

    public static Bitmap decodeFile(String pathName, Options options) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(pathName, options);

        return bitmap;
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width,
                                      int height, Matrix m, boolean filter) {
        Bitmap bitmap = null;

        try {
            bitmap = Bitmap
                    .createBitmap(source, x, y, width, height, m, filter);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "createBitmap outofmemory error");
        }

        return bitmap;
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width,
                                      int height) {
        Bitmap bitmap = null;

        try {
            bitmap = Bitmap.createBitmap(source, x, y, width, height);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "createBitmap outofmemory error");
        }

        return bitmap;
    }

    public static Bitmap createBitmap(int width, int height,
                                      Bitmap.Config config) {

        Bitmap bitmap = null;

        try {
            bitmap = Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "createBitmap outofmemory error");
        }
        return bitmap;
    }

    public Bitmap loadImage(String path, ImageLoaderListener mImageLoaderListener,
                            final ImageLoaderViewHolder mHolder) {
        SoftReference<Bitmap> refer = CACHE.get(path);
        Bitmap bm = null == refer ? null : refer.get();

        if (null == bm) {
            bm = queuePhoto(path, mImageLoaderListener, mHolder);
        }

        return bm;
    }

    public Bitmap loadImage(String path, ImageLoaderListener mImageLoaderListener,
                            final ImageLoaderViewHolder mHolder, int reqWidth, int addedScaling, int reqHeight) {
        // SoftReference<Bitmap> refer = CACHE.get(path);
        // Bitmap bm = null == refer ? null : refer.get();
        //
        // if (null == bm) {
        Bitmap bm = queuePhoto(path, mImageLoaderListener, mHolder, reqWidth, addedScaling,
                reqHeight);
        // }

        return bm;
    }

    public Bitmap queuePhoto(final String path, final ImageLoaderListener mImageLoaderListener,
                             final ImageLoaderViewHolder mHolder) {
        sFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Bitmap bm = decodeBitmap(path);
                    if (null != bm) {
                        CACHE.put(path, new SoftReference<Bitmap>(bm));
                        mImageLoaderListener.onImageLoaderListener(mHolder, bm, path);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    LOADING.remove(path);
                }
            }
        });
        return null;
    }

    public Bitmap queuePhoto(final String path, final ImageLoaderListener mImageLoaderListener,
                             final ImageLoaderViewHolder mHolder, final int reqWidth, final int addedScaling,
                             final int reqHeight) {
        sFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Bitmap bm = decodeBitmap(path, reqWidth, addedScaling, reqHeight);
                    if (null != bm) {
                        // CACHE.put(path, new SoftReference<Bitmap>(bm));
                        mImageLoaderListener.onImageLoaderListener(mHolder, bm, path);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    LOADING.remove(path);
                }
            }
        });
        return null;
    }

    @SuppressLint("NewApi")
    private Bitmap decodeBitmap(String path) {
        Bitmap bitmap;
        if (isGIF(path)) {
            bitmap = getBitmapByWidth(path, 96, 1, 96);
        } else {
            if (null == mPaths) {
                bitmap = getBitmapFormPath(path);
            } else {
                Options options = new Options();
                options.inPurgeable = true;
                options.inInputShareable = true;
                bitmap = Thumbnails.getThumbnail(mContext.getContentResolver(),
                        mIds.get(mPaths.indexOf(path)), Thumbnails.MICRO_KIND, options);
            }
        }

        return bitmap;
    }

    @SuppressLint("NewApi")
    private Bitmap decodeBitmap(String path, int reqWidth, int addedScaling, int reqHeight) {
        Bitmap bitmap;
        if (isGIF(path)) {
            bitmap = getBitmapByWidth(path, reqWidth, addedScaling, reqHeight);
        } else {
            if (null == mPaths) {
                bitmap = getBitmapByWidth(path, reqWidth, addedScaling, reqHeight);
            } else {
                Options options = new Options();
                options.inPurgeable = true;
                options.inInputShareable = true;
                bitmap = Thumbnails.getThumbnail(mContext.getContentResolver(),
                        mIds.get(mPaths.indexOf(path)), Thumbnails.MICRO_KIND, options);
            }
        }

        return bitmap;
    }

    public void queuePhotoThread(final String path) {
        if (LOADING.contains(path)) {
            return;
        }
        LOADING.add(path);
        sFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Bitmap bm = decodeBitmap(path);
                    if (null != bm) {
                        CACHE.put(path, new SoftReference<Bitmap>(bm));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    LOADING.remove(path);
                }
            }
        });
    }

    public void clearCache() {
        // clear memory cache
        CACHE.clear();
    }

    public Bitmap getBitmap(String path) {
        SoftReference<Bitmap> refer = CACHE.get(path);
        return null == refer ? null : refer.get();
    }

    public static void loadImage(String avatar_url, ImageView imageView,
                                 DisplayImageOptions optionsForUserIcon) {
        if (TextUtils.isEmpty(avatar_url) || null == ImageLoader.getInstance().getDiskCache()) {
            ImageLoader.getInstance()
                    .displayImage(avatar_url, imageView, optionsForUserIconRounded);
            return;
        }
        File imageFile = ImageLoader.getInstance().getDiskCache().get(avatar_url);
        if (null != imageFile && imageFile.exists()) {
            if (null != ImageLoader.getInstance().getMemoryCache().get(avatar_url)) {
                imageView
                        .setImageBitmap(ImageLoader.getInstance().getMemoryCache().get(avatar_url));
            } else {
                ImageSize imageSize = new ImageSize(imageView.getWidth(), imageView.getHeight());
                imageView.setImageBitmap(ImageLoader.getInstance().loadImageSync(avatar_url,
                        imageSize, optionsForUserIcon));
            }
        } else {
            ImageLoader.getInstance()
                    .displayImage(avatar_url, imageView, optionsForUserIconRounded);
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public interface ImageLoaderListener {
        void onImageLoaderListener(ImageLoaderViewHolder mHolder, Bitmap bm, String path);
    }

    public static void saveBitmap(Bitmap bitmap, String path, int compressRate) {
        FileOutputStream out;
        try {
            if (new File(path).exists()) {
                new File(path).delete();
            }
            out = new FileOutputStream(path);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, compressRate, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(Bitmap bitmap, String path, int compressRate,
                                  Bitmap.CompressFormat format) {
        FileOutputStream out;
        try {
            if (new File(path).exists()) {
                new File(path).delete();
            }
            out = new FileOutputStream(path);
            if (bitmap.compress(format, compressRate, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
