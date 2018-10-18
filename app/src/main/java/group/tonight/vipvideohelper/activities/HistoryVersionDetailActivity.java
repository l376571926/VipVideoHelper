package group.tonight.vipvideohelper.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import group.tonight.downloadmanagerhelper.DownloadManagerHelper;
import group.tonight.vipvideohelper.BR;
import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.model.VersionUpdateBean;

/**
 * 版本详情
 */
public class HistoryVersionDetailActivity extends BaseBackActivity {
    private DownloadManagerHelper mDownloadManagerHelper;

    @Override
    protected int setActivityTitle() {
        return R.string.version_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_history_version_detail);

        VersionUpdateBean versionUpdateBean = (VersionUpdateBean) getIntent().getSerializableExtra("data");

        dataBinding.setVariable(BR.data, versionUpdateBean);
        List<VersionUpdateBean.AssetsBean> assetsBeanList = versionUpdateBean.getAssets();
        if (!assetsBeanList.isEmpty()) {
            VersionUpdateBean.AssetsBean assetsBean = assetsBeanList.get(0);
            dataBinding.setVariable(BR.data1, assetsBean);

            String browser_download_url = assetsBean.getBrowser_download_url();
            if (!TextUtils.isEmpty(browser_download_url)) {
                dataBinding.setVariable(BR.handler, this);
            }
        }

        mDownloadManagerHelper = new DownloadManagerHelper(this);
    }

    public void download(View view, String downloadUrl) {
        mDownloadManagerHelper.enqueue(downloadUrl);
    }
}
