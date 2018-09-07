package group.tonight.vipvideohelper.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
            videoUrlLiveData.observe(this, new Observer<List<String>>() {
                @Override
                public void onChanged(@Nullable List<String> stringList) {
                    if (stringList == null) {
                        return;
                    }
                    mVideoUrlList = stringList;

                    SharedPreferences preferences = PrefUtils.get();
                    int index = preferences.getInt(PrefUtils.KEY_LAST_SELECT_API_INDEX, 0);
                    if (index >= mVideoUrlList.size()) {
                        index = 0;
                        preferences
                                .edit()
                                .putInt(PrefUtils.KEY_LAST_SELECT_API_INDEX, 0)
                                .apply();
                    }
                    mWebView.loadUrl(mVideoUrlList.get(index));
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
        if (item.getItemId() == R.id.action_select) {
            if (mVideoUrlList != null) {
                if (!mVideoUrlList.isEmpty()) {
                    String[] urlArray = mVideoUrlList.toArray(new String[mVideoUrlList.size()]);
                    int index = PrefUtils.get().getInt(PrefUtils.KEY_LAST_SELECT_API_INDEX, 0);
                    if (index >= urlArray.length) {
                        index = 0;
                    }
                    new AlertDialog.Builder(PlayActivity.this)
                            .setSingleChoiceItems(urlArray, index, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String url = mVideoUrlList.get(i);
                                    if (url.contains("http") && url.contains("=")) {
                                        int start = url.indexOf("http");
                                        int end = url.indexOf("=") + 1;
                                        String baseUrl = url.substring(start, end);
                                        KLog.e(baseUrl);
                                    }

                                    dialogInterface.dismiss();
                                    mWebView.loadUrl(url);
                                    PrefUtils.get()
                                            .edit()
                                            .putInt(PrefUtils.KEY_LAST_SELECT_API_INDEX, i)
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
