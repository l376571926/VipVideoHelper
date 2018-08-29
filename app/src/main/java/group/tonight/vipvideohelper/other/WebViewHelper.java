package group.tonight.vipvideohelper.other;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewHelper {
    private WebView mWebView;

    public WebViewHelper(WebView webView) {
        this.mWebView = webView;
    }

    public void setWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);//设置webview支持插件
    }

    public void setWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient());
    }
}
