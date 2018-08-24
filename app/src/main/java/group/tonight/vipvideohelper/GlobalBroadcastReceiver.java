package group.tonight.vipvideohelper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

import group.tonight.vipvideohelper.other.Consts;

public class GlobalBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                if (!intent.hasExtra(DownloadManager.EXTRA_DOWNLOAD_ID)) {
                    return;
                }
                long downloadId = PreferenceManager.getDefaultSharedPreferences(context).getLong(Consts.KEY_LAST_DOWNLOAD_ID, 0);
                long extraDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                if (extraDownloadId != downloadId) {
                    return;
                }
                //最新版本apk下载成功
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                if (downloadManager == null) {
                    return;
                }
                String mimeType = downloadManager.getMimeTypeForDownloadedFile(downloadId);
                Uri downloadedFileUri = downloadManager.getUriForDownloadedFile(downloadId);
                if (downloadedFileUri == null) {
                    return;
                }
                File file = new File(getRealFilePath(context, downloadedFileUri));
                if (!file.exists()) {
                    return;
                }
                Intent apkIntent = new Intent();
                apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                apkIntent.setAction(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri uriForFile = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                    apkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    apkIntent.setDataAndType(uriForFile, context.getContentResolver().getType(uriForFile));
                } else {
                    apkIntent.setDataAndType(Uri.fromFile(file), mimeType);
                }
                try {
                    context.startActivity(apkIntent);
                } catch (Exception var5) {
                    var5.printStackTrace();
                    Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
                }
                break;
            case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                if (intent.hasExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS)) {

                }
                break;
            case DownloadManager.ACTION_VIEW_DOWNLOADS:

                break;
            default:
                break;
        }
    }

    public String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
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
}
