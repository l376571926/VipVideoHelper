package group.tonight.vipvideohelper.activities;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.model.VersionUpdateBean;
import group.tonight.vipvideohelper.other.Consts;
import group.tonight.vipvideohelper.other.PrefUtils;
import group.tonight.vipvideohelper.other.QRCodeUtils;
import group.tonight.vipvideohelper.other.UpdateDialogHelper;
import group.tonight.vipvideohelper.other.VersionUpdateTask;
import group.tonight.vipvideohelper.worm.VipInternetWorm;

public class SettingActivity extends BaseBackActivity implements View.OnClickListener {

    private ImageView mShareAppImageView;
    private VersionUpdateBean mVersionUpdateBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setTitle("设置");

        TextView versionTextView = (TextView) findViewById(R.id.version);
        mShareAppImageView = (ImageView) findViewById(R.id.share_app);

        VersionUpdateTask versionUpdateTask = new VersionUpdateTask();
        versionUpdateTask.observe(this, new Observer<VersionUpdateBean>() {
            @Override
            public void onChanged(@Nullable VersionUpdateBean versionUpdateBean) {
                if (versionUpdateBean == null) {
                    return;
                }
                List<VersionUpdateBean.AssetsBean> assetsBeanList = versionUpdateBean.getAssets();
                if (assetsBeanList == null) {
                    return;
                }
                if (assetsBeanList.isEmpty()) {
                    return;
                }
                VersionUpdateBean.AssetsBean assetsBean = assetsBeanList.get(0);
                Bitmap bitmap = QRCodeUtils.createQRCodeWithLogo(assetsBean.getBrowser_download_url(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                mShareAppImageView.setImageBitmap(bitmap);

                if (PrefUtils.get().getInt(Consts.KEY_LAST_VERSION_ID, 0) < assetsBean.getId()) {
                    //发现新版本
                    mVersionUpdateBean = versionUpdateBean;
                }
            }
        });

        versionTextView.setOnClickListener(this);

        findViewById(R.id.parse).setOnClickListener(this);
        findViewById(R.id.manage).setOnClickListener(this);

        try {
            versionTextView.setText(getString(R.string.current_version_name_place_holder, getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manage:
                startActivity(new Intent(this, ApiManageActivity.class));
                break;
            case R.id.parse:
                new AlertDialog.Builder(this)
                        .setTitle("更新视频接口？")
                        .setMessage("更新过程可能有点久，是否继续？")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new VipInternetWorm(SettingActivity.this).execute();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case R.id.version:
                if (mVersionUpdateBean == null) {
                    Toast.makeText(SettingActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                } else {
                    new UpdateDialogHelper(SettingActivity.this, mVersionUpdateBean).show();
                }
                break;
            default:
                break;
        }
    }
}
