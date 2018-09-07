package group.tonight.vipvideohelper.other;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.socks.library.KLog;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import group.tonight.vipvideohelper.App;
import group.tonight.vipvideohelper.dao.AppRoomDatabase;
import group.tonight.vipvideohelper.dao.VipApiUrl;
import group.tonight.vipvideohelper.dao.VipApiUrlDao;
import okhttp3.Request;
import okhttp3.Response;

public class CheckTask extends AsyncTask<Void, Integer, Void> implements GenericLifecycleObserver {
    private List<VipApiUrl> vipApiUrlList;
    private WeakReference<ProgressBar> mProgressBarRef;
    private Lifecycle mLifecycle;
    private boolean mIsActivityDestroy;


    public CheckTask(List<VipApiUrl> vipApiUrlList, ProgressBar progressBar) {
        this.vipApiUrlList = vipApiUrlList;
        mProgressBarRef = new WeakReference<>(progressBar);
        ProgressBar bar = mProgressBarRef.get();
        if (bar != null) {
            bar.setMax(this.vipApiUrlList.size());
        }
        Context context = progressBar.getContext();
        if (context instanceof LifecycleOwner) {
            mLifecycle = ((LifecycleOwner) context).getLifecycle();
            mLifecycle.addObserver(this);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Integer value = values[0];
//            KLog.e(value);
        ProgressBar progressBar = mProgressBarRef.get();
        if (progressBar != null) {
            if (value == 0 || value == this.vipApiUrlList.size()) {
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
            progressBar.setProgress(value);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        VipApiUrlDao vipApiUrlDao = AppRoomDatabase.get().vipApiUrlDao();
        for (int i = 0; i < vipApiUrlList.size(); i++) {
            if (mIsActivityDestroy) {
                KLog.e("Activity已经销毁，提前结束线程");
                break;
            }
            publishProgress(i + 1);
            VipApiUrl vipApiUrl = vipApiUrlList.get(i);
            String url = vipApiUrl.getUrl();
//                KLog.e(i + " " + url);
            try {
                Response response = App.getOkHttpClient()
                        .newCall(
                                new Request.Builder()
                                        .url(url)
                                        .get()
                                        .build()
                        )
                        .execute();
                int code = response.code();
                String message = response.message();
                KLog.e("index-->" + i + " " + code + " " + message + " " + url);
                if (code == 200) {
                    vipApiUrl.setStatus(VipApiUrl.STATUS_NORMAL);
                    vipApiUrl.setUpdateTime(System.currentTimeMillis());
                    vipApiUrlDao.update(vipApiUrl);
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
                KLog.e(url + " " + e.getMessage());
            }
            vipApiUrl.setStatus(VipApiUrl.STATUS_UNUSED);
            vipApiUrl.setUpdateTime(System.currentTimeMillis());
            vipApiUrlDao.update(vipApiUrl);
        }
        return null;
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        Lifecycle.State currentState = source.getLifecycle().getCurrentState();
        if (currentState == Lifecycle.State.DESTROYED) {
//            KLog.e("Activity已销毁");
            mIsActivityDestroy = true;
            if (mLifecycle != null) {
                mLifecycle.removeObserver(this);
            }
        }
//        KLog.e(currentState + " " + event.name());
    }
}