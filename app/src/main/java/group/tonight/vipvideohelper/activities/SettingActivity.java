package group.tonight.vipvideohelper.activities;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import group.tonight.vipvideohelper.DownLoadService;
import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.VersionUpdateBean;
import group.tonight.vipvideohelper.VersionUpdater;
import group.tonight.vipvideohelper.other.Consts;
import group.tonight.vipvideohelper.other.QRCodeUtils;
import group.tonight.vipvideohelper.worm.VipInternetWorm;

public class SettingActivity extends BaseBackActivity implements View.OnClickListener {

    private TextView mVersionTextView;
    private ImageView mShareAppImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setTitle("设置");

        mVersionTextView = (TextView) findViewById(R.id.version);
        mShareAppImageView = (ImageView) findViewById(R.id.share_app);

        VersionUpdater versionUpdater = new VersionUpdater();
        versionUpdater.observe(this, new Observer<VersionUpdateBean.AssetsBean>() {
            @Override
            public void onChanged(@Nullable VersionUpdateBean.AssetsBean assetsBean) {
                if (assetsBean == null) {
                    return;
                }
                String mBrowser_download_url = assetsBean.getBrowser_download_url();

                Bitmap bitmap = QRCodeUtils.createQRCodeWithLogo(mBrowser_download_url, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                mShareAppImageView.setImageBitmap(bitmap);
            }
        });

        mVersionTextView.setOnClickListener(this);

        findViewById(R.id.parse).setOnClickListener(this);
        findViewById(R.id.manage).setOnClickListener(this);

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            mVersionTextView.setText("当前版本：" + versionName);
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
                new Thread(new VipInternetWorm(this)).start();
                break;
            case R.id.version:
                VersionUpdater versionUpdater = new VersionUpdater();
                versionUpdater.observe(SettingActivity.this, new Observer<VersionUpdateBean.AssetsBean>() {
                    @Override
                    public void onChanged(@Nullable final VersionUpdateBean.AssetsBean assetsBean) {
                        if (assetsBean == null) {
                            return;
                        }
                        final int id = assetsBean.getId();
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
                        int lastVersionId = preferences.getInt(Consts.KEY_LAST_VERSION_ID, 0);
                        if (lastVersionId < id) {
                            new AlertDialog.Builder(SettingActivity.this)
                                    .setMessage("发现新版本，是否马上更新？")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AndPermission.with(SettingActivity.this)
                                                    .runtime()
                                                    .permission(Permission.Group.STORAGE)
                                                    .onGranted(new Action<List<String>>() {
                                                        @Override
                                                        public void onAction(List<String> data) {
                                                            Intent service = new Intent(SettingActivity.this, DownLoadService.class);
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

                        } else {
                            Toast.makeText(SettingActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                break;
            default:
                break;
        }
    }
}
