package group.tonight.vipvideohelper;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;

/**
 * 更新APP
 */
public class DownLoadService extends Service {

    public static final String EXTRA_DATA = "data";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        VersionUpdateBean.AssetsBean assetsBean = (VersionUpdateBean.AssetsBean) intent.getSerializableExtra(EXTRA_DATA);
        String browser_download_url = assetsBean.getBrowser_download_url();
        String content_type = assetsBean.getContent_type();

        String DOWNLOADPATH = "/demo/apk/";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + DOWNLOADPATH + "demo.apk";
        File file = new File(path);
        if (file.exists()) {
            deleteFileWithPath(path);
        }
        try {
            DownloadManager.Request down = new DownloadManager.Request(Uri.parse(browser_download_url));
            down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                    | DownloadManager.Request.NETWORK_WIFI);
            down.setAllowedOverRoaming(false);
            down.setMimeType(content_type);
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            down.setVisibleInDownloadsUi(true);
            down.setDestinationInExternalPublicDir(DOWNLOADPATH, "demo.apk");
            down.setTitle(getString(R.string.app_name));


            DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (manager != null) {
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putLong(Consts.KEY_LAST_DOWNLOAD_ID, manager.enqueue(down))
                        .apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent0 = new Intent(Intent.ACTION_VIEW, uri);
                intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent0);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            boolean delete = f.delete();
            return true;
        }
        return false;
    }

}