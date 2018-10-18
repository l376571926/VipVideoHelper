package group.tonight.vipvideohelper;

import android.app.Application;

import com.facebook.stetho.Stetho;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import group.tonight.vipvideohelper.dao.AppRoomDatabase;
import group.tonight.vipvideohelper.other.CrashHandler;
import group.tonight.vipvideohelper.other.PrefUtils;
import group.tonight.vipvideohelper.utils.SSLSocketFactoryCompat;
import okhttp3.OkHttpClient;

public class App extends Application {
    private static OkHttpClient okHttpClient;
    private static App instance;

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Stetho.initializeWithDefaults(this);
        AppRoomDatabase.init(this);
        CrashHandler.getInstance().init(this);
        PrefUtils.init(this);

        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLSocketFactoryCompat sslSocketFactoryCompat = new SSLSocketFactoryCompat(x509TrustManager);


        okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactoryCompat, x509TrustManager)
                .build();
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}