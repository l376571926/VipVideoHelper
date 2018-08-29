package group.tonight.vipvideohelper.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.other.PrefUtils;
import group.tonight.vipvideohelper.other.WebViewHelper;

public class PlayActivity extends BaseBackActivity {
    private static final String TAG = PlayActivity.class.getSimpleName();
    private static final String DEFAULT_PARSE_URL = "http://yun.baiyug.cn/vip/index.php?url=";

    //熊孩子视频可选vip解析服务器
    private static final String HOST_1 = "http://jx.598110.com/index.php?url=";//<option value="http://jx.598110.com/index.php?url=">华南线路①</option>
    private static final String HOST_2 = "http://jx.598110.com/duo/index.php?url=";//<option value="http://jx.598110.com/duo/index.php?url=">华南线路②</option>
    private static final String HOST_3 = "http://jx.598110.com/zuida.php?url=";//<option value="http://jx.598110.com/zuida.php?url=">华北线路③</option>
    private static final String HOST_4 = "http://jx.aeidu.cn/index.php?url=";//<option value="http://jx.aeidu.cn/index.php?url=">华中线路④</option>

    private WebView mWebView;
    private ProgressDialog mDialog;
    private List<String> mVideoUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //http://jx.598110.com/index.php?url=http://m.iqiyi.com/v_19rr24dq6c.html
        //http://jx.598110.com/index.php?url=https://v.qq.com/x/cover/y23mfuucvc2ihmy/90pXxfhH6mP.html?ptag=iqiyi
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(mWebViewClient);
        WebViewHelper webViewHelper = new WebViewHelper(mWebView);
        webViewHelper.setWebView();
        webViewHelper.setWebChromeClient();

        String url = DEFAULT_PARSE_URL + "http://www.iqiyi.com/v_19rrcd03uk.html";
        if (getIntent().hasExtra("videoUrlList")) {
            mVideoUrlList = getIntent().getStringArrayListExtra("videoUrlList");
            int lastParseIndex = PrefUtils.get().getInt(PrefUtils.KEY_LAST_SELECT_API_INDEX, 0);
            if (lastParseIndex < mVideoUrlList.size()) {
                url = mVideoUrlList.get(lastParseIndex);
            } else {
                url = mVideoUrlList.get(0);
            }
        } else if (getIntent().hasExtra("videoUrl")) {
            url = getIntent().getStringExtra("videoUrl");
        }
        mWebView.loadUrl(url);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("请稍候。。。");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(TAG, "onConfigurationChanged: " + newConfig.orientation);
        boolean fullScreen = newConfig.orientation != Configuration.ORIENTATION_PORTRAIT;
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (fullScreen) {
                supportActionBar.hide();
            } else {
                supportActionBar.show();
            }
        }
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullScreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
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
                                    dialogInterface.dismiss();
                                    mWebView.loadUrl(mVideoUrlList.get(i));
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

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "onPageStarted: " + url);
            mDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "onPageFinished: " + url);
            mDialog.dismiss();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading: " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

}
