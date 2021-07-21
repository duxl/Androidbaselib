package com.duxl.baselib.download;

import android.os.Environment;

import com.duxl.baselib.BaseApplication;
import com.duxl.baselib.utils.Utils;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * 下载任务管理类
 */
public class DownLoadManager {

    /**
     * app内部下载目录（使用内部私有目录，不存在权限问题）
     */
    public static File EXTERNAL_FILE_DIR = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);


    private static DownLoadManager downLoadManager;

    private DownLoadManager() {
    }

    public static DownLoadManager getInstance() {
        initFileDownloader();
        if (downLoadManager == null) {
            downLoadManager = new DownLoadManager();
        }
        return downLoadManager;
    }

    private static void initFileDownloader() {
        if (FileDownloadHelper.getAppContext() == null) {
            FileDownloader.setup(Utils.getApp());

            // 更换下载默认实现，改为OKHttp方式下载
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(5 * 60, TimeUnit.SECONDS)
                    .writeTimeout(5 * 60, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS);

            // 添加 SSL 认证
            Utils.getApp().getGlobalHttpConfig().configurationOKHttp(builder);

            FileDownloader
                    .setupOnApplicationOnCreate(Utils.getApp())
                    .connectionCreator(new OkHttp3Connection.Creator(builder)) // 自实现 OkHttp3Connection
                    .commit();
        }
    }

    /**
     * 暂停下载，更过方法请使用 FileDownloader.getImpl()
     *
     * @param id
     * @return
     */
    public int pause(int id) {
        return FileDownloader.getImpl().pause(id);
    }

    /**
     * 暂停所有下载，更过方法请使用 FileDownloader.getImpl()
     */
    public void pauseAll() {
        FileDownloader.getImpl().pauseAll();
    }

    /**
     * 开始下载
     *
     * @param downUrl  下载文件对应的url
     * @param saveUrl  保存到本地对应的url
     * @param listener 下载监听器
     * @return
     */
    public int downloadApk(String downUrl, String saveUrl, FileDownloadLargeFileListener listener) {
        return FileDownloader.getImpl().create(downUrl)
                .setPath(saveUrl)
                .setAutoRetryTimes(3)
                .setMinIntervalUpdateSpeed(400)
                .setCallbackProgressTimes(300)
                .setListener(listener)
                .start();
    }
}
