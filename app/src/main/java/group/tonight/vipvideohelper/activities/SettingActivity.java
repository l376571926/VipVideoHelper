package group.tonight.vipvideohelper.activities;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import group.tonight.vipvideohelper.other.Consts;
import group.tonight.vipvideohelper.DownLoadService;
import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.VersionUpdateBean;
import group.tonight.vipvideohelper.VersionUpdater;

public class SettingActivity extends BaseBackActivity {

    private TextView mVersionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mVersionTextView = (TextView) findViewById(R.id.version);
        mVersionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionUpdater versionUpdater = new VersionUpdater();
                versionUpdater.observe(SettingActivity.this, new Observer<VersionUpdateBean.AssetsBean>() {
                    @Override
                    public void onChanged(@Nullable final VersionUpdateBean.AssetsBean assetsBean) {
                        if (assetsBean == null) {
                            return;
                        }
                        final String content_type = assetsBean.getContent_type();
                        final String browser_download_url = assetsBean.getBrowser_download_url();
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
            }
        });

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            mVersionTextView.setText("当前版本：" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
