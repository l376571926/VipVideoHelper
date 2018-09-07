package group.tonight.vipvideohelper.other;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import group.tonight.vipvideohelper.dao.AppRoomDatabase;
import group.tonight.vipvideohelper.dao.VipApiUrl;
import group.tonight.vipvideohelper.dao.VipApiUrlDao;


public class VideoUrlLiveData extends LiveData<List<String>> implements Runnable {
    private String vipVideoUrl;

    public VideoUrlLiveData(String vipVideoUrl) {
        this.vipVideoUrl = vipVideoUrl;
        new Thread(this).start();
    }

    @Override
    public void run() {
        VipBaseUrlProvider provider = new VipBaseUrlProvider();
        List<String> allApiUrlList = provider.getAll();
        List<String> availabel = new ArrayList<>();

        VipApiUrlDao vipApiUrlDao = AppRoomDatabase.get().vipApiUrlDao();
        for (String baseApi : allApiUrlList) {
            VipApiUrl vipApiUrl = vipApiUrlDao.findVipApiUrlByUrl(baseApi);
            if (vipApiUrl == null) {
                vipApiUrl = new VipApiUrl();
                vipApiUrl.setUrl(baseApi);
                vipApiUrlDao.insert(vipApiUrl);
            }
        }
        List<VipApiUrl> allVipApiUrls = vipApiUrlDao.getAllAvailableApiUrls();
        for (VipApiUrl url : allVipApiUrls) {
            availabel.add(url.getUrl() + this.vipVideoUrl);
        }
        postValue(availabel);
    }
}
