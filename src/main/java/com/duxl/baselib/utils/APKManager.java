package com.duxl.baselib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.duxl.baselib.R;

import java.io.File;

public class APKManager {

    /**
     * 安装apk
     *
     * @param context      上下文
     * @param apkLocalPath apk本地地址
     *                     //
     */
    public static void installApk(Context context, String apkLocalPath) {

        File apkFile = new File(apkLocalPath);
        if (!apkFile.exists()) {
            return;
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
//            if (!haveInstallPermission) {
//                Intent intent = new Intent(context, InstallAPKActivity.class);
//                intent.putExtra("filePath", apkLocalPath);
//                context.startActivity(intent);
//                return;
//            }
//        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android8.0 以上需要在Manifest中配置REQUEST_INSTALL_PACKAGES权限
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uriForFile = FileProvider.getUriForFile(context, context.getApplicationInfo().packageName + ".fileprovider", apkFile);
            intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");
            try {
                context.startActivity(intent);
            } catch (Exception var5) {
                var5.printStackTrace();
                ToastUtils.show(R.string.install_apk_failed);
            }
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

}
