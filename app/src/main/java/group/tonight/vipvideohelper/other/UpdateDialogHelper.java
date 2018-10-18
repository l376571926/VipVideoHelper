package group.tonight.vipvideohelper.other;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import group.tonight.downloadmanagerhelper.DownloadManagerHelper;
import group.tonight.vipvideohelper.model.VersionUpdateBean;

public class UpdateDialogHelper {
    private Context mContext;
    private VersionUpdateBean mVersionUpdateBean;

    public UpdateDialogHelper(Context context, VersionUpdateBean versionUpdateBean) {
        mContext = context;
        mVersionUpdateBean = versionUpdateBean;
    }

    public void show() {
        String builder = "版本号：" +
                mVersionUpdateBean.getTag_name() +
                "\n" +
                "发布时间：" +
                mVersionUpdateBean.getPublished_at() +
                "\n" +
                "更新内容：" +
                "\n" +
                mVersionUpdateBean.getBody();
        new AlertDialog.Builder(mContext)
                .setTitle("更新新版本")
//                .setMessage(mVersionUpdateBean.getBody())
                .setMessage(builder)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AndPermission.with(mContext)
                                .runtime()
                                .permission(Permission.Group.STORAGE)
                                .onGranted(new Action<List<String>>() {
                                    @Override
                                    public void onAction(List<String> data) {
                                        DownloadManagerHelper downloadManagerHelper = new DownloadManagerHelper(mContext);
                                        downloadManagerHelper.enqueue(mVersionUpdateBean.getAssets().get(0).getBrowser_download_url());
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
    }
}
