package group.tonight.vipvideohelper;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class App extends Application {
    private static OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
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
