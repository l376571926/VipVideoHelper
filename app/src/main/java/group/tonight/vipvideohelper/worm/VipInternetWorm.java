package group.tonight.vipvideohelper.worm;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.socks.library.KLog;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import group.tonight.vipvideohelper.dao.AppRoomDatabase;
import group.tonight.vipvideohelper.dao.VipApiUrl;
import group.tonight.vipvideohelper.dao.VipApiUrlDao;

/**
 * 采集站：如有乐享
 */
public class VipInternetWorm extends AsyncTask<Void, Void, Void> {
    //采集站：如有乐享
    private static final String URL_1 = "https://51.ruyo.net/3127.html";
    //采集站：岩兔站
    private static final String URL_2 = "https://yantuz.cn/466.html";

    private ProgressDialog mProgressDialog;

    public VipInternetWorm(Context context) {
        mProgressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Connection connection = Jsoup.connect(URL_1);
        try {
            Document document = connection.get();
            Elements elements = document.getElementsByTag("p");

            Set<String> urlList = new HashSet<>();
            Set<String> tempUrlList = new HashSet<>();
            for (Element element : elements) {
                String text = element.text();
                if (URLUtil.isNetworkUrl(text)) {
                    if (text.contains("=")) {
                        if (text.contains(" ")) {
                            tempUrlList.add(text);
                        } else {
                            urlList.add(text);
                        }
                    }
                }
            }
            for (String url : tempUrlList) {
                String[] split = url.split(" ");
                for (String childUrl : split) {
                    if (URLUtil.isNetworkUrl(childUrl)) {
                        if (childUrl.contains("=")) {
                            int start = childUrl.indexOf("http");
                            int end = childUrl.indexOf("=");
                            String validUrl = childUrl.substring(start, end + 1);
                            urlList.add(validUrl);
                        }
                    }
                }
            }
            KLog.e(urlList.size());
            KLog.e();

            VipApiUrlDao vipApiUrlDao = AppRoomDatabase.get().vipApiUrlDao();

            for (String url : urlList) {
                VipApiUrl vipApiUrl = vipApiUrlDao.findVipApiUrlByUrl(url);
                if (vipApiUrl == null) {
                    vipApiUrl = new VipApiUrl();
                    vipApiUrl.setUrl(url);
                    vipApiUrlDao.insert(vipApiUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Looper.prepare();
            Toast.makeText(mProgressDialog.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mProgressDialog.dismiss();
    }
}
