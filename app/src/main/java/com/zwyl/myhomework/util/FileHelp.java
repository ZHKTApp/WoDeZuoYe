package com.zwyl.myhomework.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
//import com.lz.fqh.main.one.model.notice.Image;
//import com.lz.fqh.main.one.model.notice.Video;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;

//import com.lz.fqh.main.notice.selectimage.bean.Image;
//import com.lz.fqh.main.notice.selectimage.bean.Video;

/**
 * 文件操作类
 * Created by Nereo on 2015/4/8.
 */
public class FileHelp {

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final String VIDEO_FILE_PREFIX = "VIDEO_";
    private static final String VIDEO_FILE_SUFFIX = ".mp4";

    public static boolean isVideoType(String str) {
        return ".avi rmvb .wmv .mp4 .3gp".contains(str);
    }

    public static File createTmpFile(Context context, boolean isVideo) throws IOException {
        File dir = null;
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (!dir.exists()) {
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
                if (!dir.exists()) {
                    dir = getCacheDirectory(context, true);
                }
            }
        } else {
            dir = getCacheDirectory(context, true);
        }
        return isVideo ? File.createTempFile(VIDEO_FILE_PREFIX, VIDEO_FILE_SUFFIX, dir) : File.createTempFile(JPEG_FILE_PREFIX, JPEG_FILE_SUFFIX, dir);
    }


    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
     * on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    private static final int UNIT_MB = 1024 * 1024;
    public static final int ALLOW_VIDEO_MAX_SIZE = 10 * UNIT_MB;
    public static File getCacheBitmapFromUrl(Context ctx, String url) {
        File file = new File(getCacheFileDir(ctx), InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new NullPointerException("创建 图片缓存目录 失败，注意 6.0+ 的动态申请权限");
            }
        }
        String[] temp = url.split("/");
        return new File(file, temp[temp.length - 1]);
    }

    public static File getCacheVideoFromUrl(Context ctx, String url) {
        File file = getCacheDirectory(ctx, true);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new NullPointerException("创建 图片缓存目录 失败，注意 6.0+ 的动态申请权限");
            }
        }
        String[] temp = url.split("/");
        return new File(file, temp[temp.length - 1]);
    }

    public static File getCacheFileDir(Context ctx) {
        File cacheDir = ctx.getCacheDir();
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                throw new NullPointerException("创建 缓存目录 失败，注意 6.0+ 的动态申请权限");
            }
        }
        return cacheDir;
    }

    //    Bitmap对象保存为文件
    public static void saveBitmap2File(Bitmap bitmap, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

}
