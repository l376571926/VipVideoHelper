package group.tonight.vipvideohelper;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import group.tonight.vipvideohelper.other.CrashHandler;
import group.tonight.vipvideohelper.other.PrefUtils;
import okhttp3.OkHttpClient;

public class App extends Application {
    private static OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        PrefUtils.init(this);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2000, TimeUnit.MILLISECONDS)
                .readTimeout(2000, TimeUnit.MILLISECONDS)
                .writeTimeout(2000, TimeUnit.MILLISECONDS)
                .build();
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
