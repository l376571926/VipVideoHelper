package group.tonight.vipvideohelper.other;

import android.arch.lifecycle.LiveData;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.socks.library.KLog;

import java.io.IOException;
import java.util.List;

import group.tonight.vipvideohelper.App;
import group.tonight.vipvideohelper.model.VersionUpdateBean;
import okhttp3.Request;
import okhttp3.Response;

public class VersionUpdateTask extends LiveData<VersionUpdateBean> implements Runnable {
    //获取所有发布的版本
    public static final String ALL = "https://api.github.com/repos/l376571926/VipVideoHelper/releases";
    //最新版apk地址
    private static final String LATEST = "https://api.github.com/repos/l376571926/VipVideoHelper/releases/latest";

    public VersionUpdateTask() {
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
            postValue(versionUpdateBean);

            List<VersionUpdateBean.AssetsBean> assetsBeanList = versionUpdateBean.getAssets();
            if (assetsBeanList == null) {
                return;
            }
            if (assetsBeanList.isEmpty()) {
                return;
            }
            postValue(versionUpdateBean);
        } catch (IOException e) {
            e.printStackTrace();
            KLog.e("run: " + e.getMessage());
            Looper.prepare();
            Toast.makeText(App.get(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }
}
