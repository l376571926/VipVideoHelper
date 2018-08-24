package group.tonight.vipvideohelper;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import group.tonight.vipvideohelper.other.VipBaseUrlProvider;


public class VideoUrlLiveData extends LiveData<List<String>> implements Runnable {
    private static final String TAG = VideoUrlLiveData.class.getSimpleName();
    private String vipVideoUrl;

    public VideoUrlLiveData(String vipVideoUrl) {
        this.vipVideoUrl = vipVideoUrl;
        new Thread(this).start();
    }

    @Override
    public void run() {
        VipBaseUrlProvider provider = new VipBaseUrlProvider();
        List<String> all = provider.getAll();
        String vipVideoUrl = provider.getTestVipVideoUrl();

        List<String> availabel = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            String videoUrl = all.get(i) + this.vipVideoUrl;
//            Request request = new Request.Builder()
//                    .url(videoUrl)
//                    .build();
//            try {
//                Response response = App.getOkHttpClient().newCall(request).execute();
//                int code = response.code();
//                String message = response.message();

//                if (code == 200) {//过滤非200请求
//                    ResponseBody responseBody = response.body();
//                    MediaType mediaType = responseBody.contentType();
//                    long contentLength = responseBody.contentLength();
//                    Log.e(TAG, "doInBackground: " + i + " " + code + " " + message + " " + mediaType + " " + contentLength + " " + videoUrl);
            availabel.add(videoUrl);
//                }

//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d(TAG, "doInBackground: " + i + " " + videoUrl + " " + e.getMessage());
//            }
        }
        postValue(availabel);
    }
}
