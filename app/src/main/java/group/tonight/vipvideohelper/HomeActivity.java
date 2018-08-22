package group.tonight.vipvideohelper;

import android.app.DownloadManager;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private WebView mWebView;
    private String mCurrentVideoUrl;
    private TextView mWebUrlView;
    private ProgressDialog mProgressDialog;
    private SharedPreferences mPreferences;
    private DownloadManager mDownloadManager;

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
                parse();
            }
        });


        mWebUrlView = (TextView) findViewById(R.id.web_url);
        //http://jx.598110.com/index.php?url=https://v.qq.com/x/cover/y23mfuucvc2ihmy/90pXxfhH6mP.html?ptag=iqiyi
        mWebView = (WebView) findViewById(R.id.web_view);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);


        String url = "http://m.iqiyi.com/";
//        String url = "www.pokonyan.cn/video/index.html";
        mWebView.loadUrl(url);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        VersionUpdater versionUpdater = new VersionUpdater();
        versionUpdater.observe(this, new Observer<VersionUpdateBean.AssetsBean>() {
            @Override
            public void onChanged(@Nullable final VersionUpdateBean.AssetsBean assetsBean) {
                if (assetsBean == null) {
                    return;
                }
                final String content_type = assetsBean.getContent_type();
                final String browser_download_url = assetsBean.getBrowser_download_url();
                int id = assetsBean.getId();
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

    private void parse() {
        mProgressDialog.show();
        VideoUrlLiveData liveData = new VideoUrlLiveData(mCurrentVideoUrl);
        liveData.observe(HomeActivity.this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> strings) {
                mProgressDialog.dismiss();
                if (strings == null) {
                    return;
                }
                String[] urlArray = strings.toArray(new String[strings.size()]);
                new AlertDialog.Builder(HomeActivity.this)
                        .setSingleChoiceItems(urlArray, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                                intent.putExtra("videoUrl", strings.get(i));
                                startActivityForResult(intent, 0);

                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e(TAG, "onPageStarted: " + url);
            mProgressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "onPageFinished: " + url);
            mCurrentVideoUrl = url;
            mWebUrlView.setText(url);
            mProgressDialog.dismiss();
            if (url.endsWith(".html")) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setMessage("检测到可播放视频，是否开始解析？")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                parse();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Log.d(TAG, "onLoadResource: " + url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e(TAG, "shouldOverrideUrlLoading: " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

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
        menu.add("帮助").setIcon(R.drawable.help_circle_outline).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("帮助")) {
            new AlertDialog.Builder(this)
                    .setMessage("网页中找到要播放的视频，并点击进入，但是不播放，因为播你也播不了，然后点击右下角圆形按钮，进入新界面，稍等片刻，等界面中出现播放按钮，就可以播放了")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}
