package group.tonight.vipvideohelper.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.dao.AppRoomDatabase;
import group.tonight.vipvideohelper.dao.VipApiUrl;
import group.tonight.vipvideohelper.other.PrefUtils;
import group.tonight.vipvideohelper.other.VideoUrlLiveData;
import group.tonight.vipvideohelper.other.VipBaseUrlProvider;
import group.tonight.vipvideohelper.other.WebViewHelper;

public class PlayActivity extends BaseBackActivity {
    private WebView mWebView;
    private List<String> mVideoUrlList = new ArrayList<>();
    private String mCurrentVideoUrl;

    @Override
    protected int setActivityTitle() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mWebView = (WebView) findViewById(R.id.web_view);
        WebViewHelper webViewHelper = new WebViewHelper(mWebView);
        webViewHelper.init();

        if (getIntent().hasExtra("videoUrl")) {
            mCurrentVideoUrl = getIntent().getStringExtra("videoUrl");
            VideoUrlLiveData videoUrlLiveData = new VideoUrlLiveData(mCurrentVideoUrl);
            videoUrlLiveData.setOnlyApiUrl(true);
            videoUrlLiveData.observe(this, new Observer<List<String>>() {
                @Override
                public void onChanged(@Nullable List<String> stringList) {
                    if (stringList == null) {
                        return;
                    }
                    mVideoUrlList = stringList;
                    int urlIndex = mVideoUrlList.indexOf(PrefUtils.get().getString(PrefUtils.KEY_LAST_SELECT_API_URL, ""));
                    if (urlIndex == -1) {
                        urlIndex = 0;
                    }
                    mWebView.loadUrl(mVideoUrlList.get(urlIndex) + mCurrentVideoUrl);
                }
            });
        }
        LiveData<List<VipApiUrl>> liveData = AppRoomDatabase.get().vipApiUrlDao().getAllAvailableApiUrlsLiveData();
        liveData.observe(PlayActivity.this, new Observer<List<VipApiUrl>>() {
            @Override
            public void onChanged(@Nullable List<VipApiUrl> vipApiUrls) {
                if (vipApiUrls == null) {
                    return;
                }
                KLog.e("更新可用接口列表");
                mVideoUrlList.clear();
                for (VipApiUrl vipApiUrl : vipApiUrls) {
                    mVideoUrlList.add(vipApiUrl.getUrl() + mCurrentVideoUrl);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String lastParseApiUrl = PrefUtils.get().getString(PrefUtils.KEY_LAST_SELECT_API_URL, "");
        if (item.getItemId() == R.id.action_recommend) {
            final List<String> urlList = new ArrayList<>();
            urlList.add(VipBaseUrlProvider.DEFAULT_VIP_URL_0);
            urlList.add(VipBaseUrlProvider.DEFAULT_VIP_URL_1);
            urlList.add(VipBaseUrlProvider.DEFAULT_VIP_URL_5);
            urlList.add(VipBaseUrlProvider.DEFAULT_VIP_URL_2);//有广告
            urlList.add(VipBaseUrlProvider.DEFAULT_VIP_URL_3);//有广告
            urlList.add(VipBaseUrlProvider.DEFAULT_VIP_URL_4);//有广告

            int index = urlList.indexOf(lastParseApiUrl);
            if (index == -1) {
                index = 0;
            }
            new AlertDialog.Builder(this)
                    .setTitle("优质播放源")
                    .setSingleChoiceItems(urlList.toArray(new String[urlList.size()]), index, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            String apiUrl = urlList.get(which);
                            mWebView.loadUrl(apiUrl + mCurrentVideoUrl);
                            PrefUtils.get()
                                    .edit()
                                    .putString(PrefUtils.KEY_LAST_SELECT_API_URL, apiUrl)
                                    .apply();
                        }
                    })
                    .show();

        } else if (item.getItemId() == R.id.action_normal) {
            if (mVideoUrlList != null) {
                if (!mVideoUrlList.isEmpty()) {
                    int index = mVideoUrlList.indexOf(lastParseApiUrl);
                    if (index == -1) {
                        index = 0;
                    }
                    new AlertDialog.Builder(PlayActivity.this)
                            .setTitle("普通播放源")
                            .setSingleChoiceItems(mVideoUrlList.toArray(new String[mVideoUrlList.size()]), index, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    mWebView.loadUrl(mVideoUrlList.get(i) + mCurrentVideoUrl);
                                    PrefUtils.get()
                                            .edit()
                                            .putString(PrefUtils.KEY_LAST_SELECT_API_URL,mVideoUrlList.get(i))
                                            .apply();
                                }
                            })
                            .show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        KLog.e("onConfigurationChanged: " + newConfig.orientation);

        WebViewHelper.fullScreen(this, false);
    }
}
