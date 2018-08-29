package group.tonight.vipvideohelper.activities;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

import group.tonight.vipvideohelper.DownLoadService;
import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.VersionUpdateBean;
import group.tonight.vipvideohelper.VersionUpdater;
import group.tonight.vipvideohelper.VideoUrlLiveData;
import group.tonight.vipvideohelper.other.Consts;
import group.tonight.vipvideohelper.other.WebViewHelper;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private WebView mWebView;
    private String mCurrentVideoUrl;
    private TextInputEditText mWebUrlTextView;
    private ProgressDialog mProgressDialog;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.setMessage("请稍候。。。");
                mProgressDialog.show();
                VideoUrlLiveData liveData = new VideoUrlLiveData(mCurrentVideoUrl);
                liveData.observe(HomeActivity.this, new Observer<List<String>>() {
                    @Override
                    public void onChanged(@Nullable final List<String> strings) {
                        mProgressDialog.dismiss();
                        if (strings == null) {
                            return;
                        }
                        Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                        intent.putStringArrayListExtra("videoUrlList", new ArrayList<>(strings));
                        startActivityForResult(intent, 0);
                    }
                });
            }
        });


        mWebUrlTextView = (TextInputEditText) findViewById(R.id.web_url);
        mWebUrlTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String url = v.getText().toString();
                    Log.e(TAG, "onEditorAction: " + url);
                    if (!TextUtils.isEmpty(url)) {
                        mWebView.loadUrl(url);
                    }
                }
                return false;
            }
        });


        //http://jx.598110.com/index.php?url=https://v.qq.com/x/cover/y23mfuucvc2ihmy/90pXxfhH6mP.html?ptag=iqiyi
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(mWebViewClient);

        WebViewHelper webViewHelper = new WebViewHelper(mWebView);
        webViewHelper.setWebView();
        webViewHelper.setWebChromeClient();

        String url = "http://m.iqiyi.com/";
//        String url = "www.pokonyan.cn/video/index.html";
        mWebView.loadUrl(url);

        mProgressDialog = new ProgressDialog(this);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        VersionUpdater versionUpdater = new VersionUpdater();
        versionUpdater.observe(this, new Observer<VersionUpdateBean.AssetsBean>() {
            @Override
            public void onChanged(@Nullable final VersionUpdateBean.AssetsBean assetsBean) {
                if (assetsBean == null) {
                    return;
                }
                final int id = assetsBean.getId();
                int lastVersionId = mPreferences.getInt(Consts.KEY_LAST_VERSION_ID, 0);
                if (lastVersionId < id) {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setMessage("发现新版本，是否马上更新？")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AndPermission.with(HomeActivity.this)
                                            .runtime()
                                            .permission(Permission.Group.STORAGE)
                                            .onGranted(new Action<List<String>>() {
                                                @Override
                                                public void onAction(List<String> data) {
                                                    Intent service = new Intent(HomeActivity.this, DownLoadService.class);
                                                    service.putExtra(DownLoadService.EXTRA_DATA, assetsBean);
                                                    startService(service);
                                                }
                                            })
                                            .start();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();

                }
            }
        });
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "onPageStarted: " + url);
            mProgressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "onPageFinished: " + url);
            mCurrentVideoUrl = url;
            mWebUrlTextView.setText(url);
            mProgressDialog.dismiss();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e(TAG, "shouldOverrideUrlLoading: " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

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
        getMenuInflater().inflate(R.menu.menu_home_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.action_help:
                new AlertDialog.Builder(this)
                        .setMessage("网页中找到要播放的视频，并点击进入，但是不播放，因为播你也播不了，然后点击右下角圆形按钮，进入新界面，稍等片刻，等界面中出现播放按钮，就可以播放了")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case R.id.action_select_site:
                final String[] urlArray = {
                        "http://m.iqiyi.com/",
                        "http://m.v.qq.com/",
                        "https://www.youku.com",
                };
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(urlArray, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWebView.loadUrl(urlArray[which]);
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
