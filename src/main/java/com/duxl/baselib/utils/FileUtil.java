package com.duxl.baselib.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件工具类
 */
public class FileUtil {

    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

//    /**
//     * 检查文件是否存在
//     */
//    public static String checkDirPath(String dirPath) {
//        if (TextUtils.isEmpty(dirPath)) {
//            return "";
//        }
//        File dir = new File(dirPath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        return dirPath;
//    }

    /**
     * 从assets目录读取文件
     *
     * @param context
     * @param file    assets目录下的文件，例如："data/area.json"
     * @return
     * @throws IOException
     */
    public static String loadAsset(Context context, String file) throws IOException {
        StringBuffer sb = new StringBuffer();
        InputStream is = context.getAssets().open(file);
        byte[] buffer = new byte[512];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len));
        }
        is.close();
        return sb.toString();
    }

    public static void copyFile(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[512];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

        if (os != null) {
            os.flush();
            os.close();
        }

        if (is != null) {
            is.close();
        }
    }

    /**
     * 复制文件
     *
     * @param from 需要复制的文件
     * @param to   复制到目标位置
     * @throws IOException
     */
    public static void copyFile(File from, File to) throws IOException {
        if (!to.getParentFile().exists()) {
            to.getParentFile().mkdirs();
        }
        InputStream is = new FileInputStream(from);
        OutputStream os = new FileOutputStream(to);
        copyFile(is, os);

    }

    /**
     * 复制文件
     *
     * @param context
     * @param from
     * @param to
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void copyFile(Context context, Uri from, File to) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(from);
        OutputStream os = new FileOutputStream(to);
        copyFile(is, os);
    }

    /**
     * 复制文件
     *
     * @param context
     * @param from
     * @param to
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void copyFile(Context context, File from, Uri to) throws IOException {
        InputStream is = new FileInputStream(from);
        OutputStream os = context.getContentResolver().openOutputStream(to);
        copyFile(is, os);
    }

    /**
     * 复制文件
     *
     * @param context
     * @param from
     * @param to
     */
    public static void copyFile(Context context, Uri from, Uri to) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(from);
        OutputStream os = context.getContentResolver().openOutputStream(to);
        copyFile(is, os);
    }
}
