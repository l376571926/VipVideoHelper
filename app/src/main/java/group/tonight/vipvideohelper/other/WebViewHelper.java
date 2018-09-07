package group.tonight.vipvideohelper.other;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.socks.library.KLog;

import java.io.IOException;

import group.tonight.vipvideohelper.App;
import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.dao.AppRoomDatabase;
import group.tonight.vipvideohelper.dao.VipApiUrl;
import group.tonight.vipvideohelper.dao.VipApiUrlDao;
import okhttp3.Request;
import okhttp3.Response;

public class WebViewHelper {
    private final WebView mWebView;
    private final ProgressDialog mProgressDialog;
    private IWebViewClient mIWebViewClient;
    private String mCurrentApiUrl;
    private Context mContext;
    private final ViewGroup mVideoContainer;

    public WebViewHelper(WebView webView) {
        this.mWebView = webView;
        mContext = webView.getContext();
        mProgressDialog = new ProgressDialog(mContext);
        mVideoContainer = webView.getRootView().findViewById(R.id.video_container);
    }

    /**
     * https://blog.csdn.net/qq_34123326/article/details/79023262
     * 解析腾讯视频部分网页点击无效的bug
     */
    public void init() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setGeolocationEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWebView.requestFocus();
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            super.onShowCustomView(view, requestedOrientation, callback);
            KLog.e();
        }

        /**
         * 在视频中点击全屏按钮时调用这个方法
         * @param view
         * @param callback
         */
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            KLog.e();
            fullScreen(mContext, true);
            if (mVideoContainer != null) {
                mVideoContainer.removeAllViews();
                mVideoContainer.addView(view);
            }
        }


        /**
         * 退出全屏视频模式时会调用这个方法
         */
        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            KLog.e();
            fullScreen(mContext, true);
            if (mVideoContainer != null) {
                mVideoContainer.removeAllViews();
            }
        }
    };

    public void setIWebViewClient(IWebViewClient iWebViewClient) {
        this.mIWebViewClient = iWebViewClient;
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            KLog.e(url);
            if (url.contains("http") && url.contains("=")) {
                int start = url.indexOf("http");
                int end = url.indexOf("=") + 1;
                mCurrentApiUrl = url.substring(start, end);
            }
            mProgressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            KLog.e(url);
            mProgressDialog.dismiss();
            if (mIWebViewClient != null) {
                mIWebViewClient.onPageFinished(view, url);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                KLog.e("shouldOverrideUrlLoading: " + request.getUrl());
            } else {
                KLog.e();
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            KLog.e("shouldOverrideUrlLoading: " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KLog.e(error.getErrorCode() + " " + error.getDescription());
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            KLog.e(errorCode + " " + description + " " + failingUrl);
        }

        @Override
        public void onReceivedHttpError(final WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Uri url = request.getUrl();
                KLog.e(errorResponse.getStatusCode() + " " + errorResponse.getReasonPhrase() + " " + url.toString());
                Toast.makeText(view.getContext(), errorResponse.getReasonPhrase(), Toast.LENGTH_SHORT).show();

                if (mCurrentApiUrl != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Response response = App.getOkHttpClient()
                                        .newCall(
                                                new Request.Builder()
                                                        .url(mCurrentApiUrl)
                                                        .get()
                                                        .build()
                                        )
                                        .execute();
                                if (response.code() == 200) {
                                    KLog.e("解析接口有效，视频链接无法用于此接口");
                                    mWebView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(view.getContext().getApplicationContext(), "解析接口有效，视频链接无法用于此接口", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            VipApiUrlDao vipApiUrlDao = AppRoomDatabase.get().vipApiUrlDao();
                            VipApiUrl vipApiUrl = vipApiUrlDao.findVipApiUrlByUrl(mCurrentApiUrl);
                            if (vipApiUrl != null) {
                                vipApiUrl.setStatus(VipApiUrl.STATUS_UNUSED);
                                vipApiUrl.setUpdateTime(System.currentTimeMillis());
                                vipApiUrlDao.update(vipApiUrl);
                            }
                        }
                    }).start();
                }
            } else {
                KLog.e();
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            KLog.e(error);
        }
    };

    public interface IWebViewClient {
        void onPageFinished(WebView view, String url);
    }

    public static void fullScreen(Context context, boolean adjustOrientation) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            Configuration configuration = activity.getResources().getConfiguration();
            int orientation = configuration.orientation;

            boolean fullScreen = orientation != Configuration.ORIENTATION_PORTRAIT;
            if (adjustOrientation) {
                if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullScreen = false;
                } else {
                    fullScreen = true;
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
            Window activityWindow = activity.getWindow();

            WindowManager.LayoutParams attrs = activityWindow.getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                activityWindow.setAttributes(attrs);
                activityWindow.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activityWindow.setAttributes(attrs);
                activityWindow.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }
}
