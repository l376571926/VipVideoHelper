package group.tonight.vipvideohelper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ParseHostTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = ParseHostTask.class.getSimpleName();
    private static final String XIONG_HAI_ZI = "http://www.pokonyan.cn/video/index.html";
    private static final String aaaa = "http://www.pokonyan.cn/video/js/index.js?v1.3";

    @Override
    protected Void doInBackground(Void... voids) {
        VipBaseUrlProvider provider = new VipBaseUrlProvider();
        List<String> all = provider.getAll();
        String vipVideoUrl = provider.getTestVipVideoUrl();

        for (int i = 0; i < all.size(); i++) {
            String videoUrl = all.get(i) + vipVideoUrl;
            Request request = new Request.Builder()
                    .url(videoUrl)
                    .build();
            try {
                Response response = App.getOkHttpClient().newCall(request).execute();

                ResponseBody responseBody = response.body();
                MediaType mediaType = responseBody.contentType();
                long contentLength = responseBody.contentLength();
                Log.e(TAG, "doInBackground: " + i + " " + mediaType + " " + contentLength + " " + videoUrl);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: " + videoUrl + " " + e.getMessage());
            }
        }


//        okHttpClient.newCall(request)
//                .enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        String string = response.body().string();
//
//                        String str = "abc3443abcfgjhgabcgfjabc";
////                        String rgex = "xianlu=(.*?);";
//                        String regex = "(?<=\\[)(\\S+)(?=\\])"; //匹配方括号
//                        List<String> subUtil = getSubUtil(string.replace(" ", ""), regex);
//
//                        Log.e(TAG, "onResponse: ");
//                    }
//                });

//            Document document = Jsoup.connect(XIONG_HAI_ZI)
//                    .header("Accept-Encoding", "gzip, deflate")
//                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
//                    .maxBodySize(0)
//                    .timeout(600000)
//                    .get();
//            Elements selects = document.getElementsByClass("select");
//            Elements options = document.getElementsByTag("option");
//            Elements elements = document.select("option");
//            Elements optionss = document.select("select > option");
//            int size = selects.size();
//            int size1 = options.size();
//            if (size == 0) {
//                return null;
//            }
//            Log.e(TAG, "doInBackground: ");


//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    /**
     * 正则表达式匹配两个指定字符串中间的内容
     *
     * @param soap
     * @return
     */
    public static List<String> getSubUtil(String soap, String rgex) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;
    }


}
