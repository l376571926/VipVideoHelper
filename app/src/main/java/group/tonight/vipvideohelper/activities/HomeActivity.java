package group.tonight.vipvideohelper.activities;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.socks.library.KLog;

import java.util.List;

import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.model.VersionUpdateBean;
import group.tonight.vipvideohelper.other.Consts;
import group.tonight.vipvideohelper.other.PrefUtils;
import group.tonight.vipvideohelper.other.QRCodeUtils;
import group.tonight.vipvideohelper.other.UpdateDialogHelper;
import group.tonight.vipvideohelper.other.VersionUpdateTask;
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

        IconicsDrawable iconicsDrawable = new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_airplane)
                .color(Color.WHITE);
        fab.setImageDrawable(iconicsDrawable);

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

        VersionUpdateTask versionUpdateTask = new VersionUpdateTask();
        versionUpdateTask.observe(this, new Observer<VersionUpdateBean>() {
            @Override
            public void onChanged(@Nullable VersionUpdateBean versionUpdateBean) {
                if (versionUpdateBean == null) {
                    return;
                }
                String tag_name = versionUpdateBean.getTag_name();//线上版本号
                if (tag_name == null) {
                    return;
                }
                try {
                    if (tag_name.compareTo(getPackageManager().getPackageInfo(getPackageName(), 0).versionName) != 0) {
                        //发现新版本
                        new UpdateDialogHelper(HomeActivity.this, versionUpdateBean).show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
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
