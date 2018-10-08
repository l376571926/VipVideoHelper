package group.tonight.vipvideohelper.other;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import group.tonight.vipvideohelper.services.DownLoadService;
import group.tonight.vipvideohelper.model.VersionUpdateBean;

public class UpdateDialogHelper {
    private Context mContext;
    private VersionUpdateBean mVersionUpdateBean;

    public UpdateDialogHelper(Context context, VersionUpdateBean versionUpdateBean) {
        mContext = context;
        mVersionUpdateBean = versionUpdateBean;
    }

    public void show() {
        StringBuilder builder = new StringBuilder();
        builder.append("版本号：")
                .append(mVersionUpdateBean.getTag_name())
                .append("\n")
                .append("发布时间：")
                .append(mVersionUpdateBean.getPublished_at())
                .append("\n")
                .append("更新内容：")
                .append("\n")
                .append(mVersionUpdateBean.getBody());
        new AlertDialog.Builder(mContext)
                .setTitle("更新新版本")
//                .setMessage(mVersionUpdateBean.getBody())
                .setMessage(builder.toString())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AndPermission.with(mContext)
                                .runtime()
                                .permission(Permission.Group.STORAGE)
                                .onGranted(new Action<List<String>>() {
                                    @Override
                                    public void onAction(List<String> data) {
                                        Intent service = new Intent(mContext, DownLoadService.class);
                                        service.putExtra(DownLoadService.EXTRA_DATA, mVersionUpdateBean.getAssets().get(0));
                                        mContext.startService(service);
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
