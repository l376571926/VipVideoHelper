package group.tonight.vipvideohelper.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.socks.library.KLog;

import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.other.WebViewHelper;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private WebView mWebView;
    private String mCurrentVideoUrl;
    private EditText mWebUrlTextView;
    private String[] mVideoUrlArrays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                intent.putExtra("videoUrl", mCurrentVideoUrl);
                startActivity(intent);
            }
        });

        mWebUrlTextView = (EditText) findViewById(R.id.web_url);
        mWebUrlTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String url = v.getText().toString();
                    KLog.e("onEditorAction: " + url);
                    if (!TextUtils.isEmpty(url)) {
                        mWebView.loadUrl(url);
                    }
                }
                return false;
            }
        });
        // TODO: 2018/8/29 替换为腾讯X5WebView
        //http://jx.598110.com/index.php?url=https://v.qq.com/x/cover/y23mfuucvc2ihmy/90pXxfhH6mP.html?ptag=iqiyi
        mWebView = (WebView) findViewById(R.id.web_view);

        WebViewHelper webViewHelper = new WebViewHelper(mWebView);
        webViewHelper.init();
        webViewHelper.setIWebViewClient(new WebViewHelper.IWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mCurrentVideoUrl = url;
                mWebUrlTextView.setText(url);
            }
        });
        mVideoUrlArrays = getResources().getStringArray(R.array.defaults_video_url_array);
        mWebView.loadUrl(mVideoUrlArrays[0]);
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
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(mVideoUrlArrays, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWebView.loadUrl(mVideoUrlArrays[which]);
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
