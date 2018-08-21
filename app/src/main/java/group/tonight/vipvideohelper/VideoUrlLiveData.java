package group.tonight.vipvideohelper;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class VideoUrlLiveData extends LiveData<List<String>> implements Runnable {
    private static final String TAG = VideoUrlLiveData.class.getSimpleName();
    private OkHttpClient okHttpClient;
    private String vipVideoUrl;

    public VideoUrlLiveData(String vipVideoUrl) {
        this.vipVideoUrl = vipVideoUrl;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.MILLISECONDS)
                .readTimeout(50, TimeUnit.MILLISECONDS)
                .writeTimeout(50, TimeUnit.MILLISECONDS)
                .build();

        new Thread(this).start();
    }

    @Override
    protected void onActive() {
        super.onActive();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
    }

    @Override
    public void run() {
        VipBaseUrlProvider provider = new VipBaseUrlProvider();
        List<String> all = provider.getAll();
        String vipVideoUrl = provider.getTestVipVideoUrl();

        List<String> availabel = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            String videoUrl = all.get(i) + this.vipVideoUrl;
            Request request = new Request.Builder()
                    .url(videoUrl)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();

                ResponseBody responseBody = response.body();
                MediaType mediaType = responseBody.contentType();
                long contentLength = responseBody.contentLength();
                Log.e(TAG, "doInBackground: " + i + " " + mediaType + " " + contentLength + " " + videoUrl);

                availabel.add(videoUrl);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: " + i + " " + videoUrl + " " + e.getMessage());
            }
        }
        postValue(availabel);
    }
}
