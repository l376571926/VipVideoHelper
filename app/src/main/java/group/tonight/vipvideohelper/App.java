package group.tonight.vipvideohelper;

import android.app.Application;

import com.facebook.stetho.Stetho;

import java.util.concurrent.TimeUnit;

import group.tonight.vipvideohelper.dao.AppRoomDatabase;
import group.tonight.vipvideohelper.other.CrashHandler;
import group.tonight.vipvideohelper.other.PrefUtils;
import okhttp3.OkHttpClient;

public class App extends Application {
    private static OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        AppRoomDatabase.init(this);
        CrashHandler.getInstance().init(this);
        PrefUtils.init(this);
        okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(2000, TimeUnit.MILLISECONDS)
//                .readTimeout(2000, TimeUnit.MILLISECONDS)
//                .writeTimeout(2000, TimeUnit.MILLISECONDS)
                .build();
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                Log.d("app", " onViewInitFinished is " + arg0);
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//            }
//        };
//        //x5内核初始化接口
//        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
