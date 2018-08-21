package group.tonight.vipvideohelper;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DEFAULT_PARSE_URL = "http://yun.baiyug.cn/vip/index.php?url=";

    //熊孩子视频可选vip解析服务器
    private static final String HOST_1 = "http://jx.598110.com/index.php?url=";//<option value="http://jx.598110.com/index.php?url=">华南线路①</option>
    private static final String HOST_2 = "http://jx.598110.com/duo/index.php?url=";//<option value="http://jx.598110.com/duo/index.php?url=">华南线路②</option>
    private static final String HOST_3 = "http://jx.598110.com/zuida.php?url=";//<option value="http://jx.598110.com/zuida.php?url=">华北线路③</option>
    private static final String HOST_4 = "http://jx.aeidu.cn/index.php?url=";//<option value="http://jx.aeidu.cn/index.php?url=">华中线路④</option>

    private WebView mWebView;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //http://jx.598110.com/index.php?url=http://m.iqiyi.com/v_19rr24dq6c.html
        //http://jx.598110.com/index.php?url=https://v.qq.com/x/cover/y23mfuucvc2ihmy/90pXxfhH6mP.html?ptag=iqiyi
        mWebView = (WebView) findViewById(R.id.web_view);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);

        String url = HOST_1 + "http://www.iqiyi.com/v_19rrcd03uk.html";
        if (getIntent().hasExtra("videoUrl")) {
            url = getIntent().getStringExtra("videoUrl");
            url = HOST_1 + url;
        }
        mWebView.loadUrl(url);

        mDialog = new ProgressDialog(this);
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

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e(TAG, "onPageStarted: " + url);
            mDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "onPageFinished: " + url);
            mDialog.dismiss();
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
}
