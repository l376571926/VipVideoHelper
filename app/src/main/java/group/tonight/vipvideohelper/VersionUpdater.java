package group.tonight.vipvideohelper;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class VersionUpdater extends LiveData<VersionUpdateBean.AssetsBean> implements Runnable {
    //获取所有发布的版本
    private static final String ALL = "https://api.github.com/repos/l376571926/VipVideoHelper/releases";
    //最新版apk地址
    private static final String LATEST = "https://api.github.com/repos/l376571926/VipVideoHelper/releases/latest";
    private static final String TAG = VersionUpdater.class.getSimpleName();

    public VersionUpdater() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        Request request = new Request.Builder()
                .get()
                .url(LATEST)
                .build();
        try {
            Response response = App.getOkHttpClient().newCall(request).execute();
            int code = response.code();
            if (code != 200) {
                return;
            }
            String json = response.body().string();
            VersionUpdateBean versionUpdateBean = new Gson().fromJson(json, VersionUpdateBean.class);
            if (versionUpdateBean == null) {
                return;
            }
            List<VersionUpdateBean.AssetsBean> assetsBeanList = versionUpdateBean.getAssets();
            if (assetsBeanList == null) {
                return;
            }
            if (assetsBeanList.isEmpty()) {
                return;
            }
            VersionUpdateBean.AssetsBean assetsBean = assetsBeanList.get(0);
            String browser_download_url = assetsBean.getBrowser_download_url();
            int size = assetsBean.getSize();
            int download_count = assetsBean.getDownload_count();
            String content_type = assetsBean.getContent_type();

            postValue(assetsBean);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "run: " + e.getMessage());
        }
    }
}
