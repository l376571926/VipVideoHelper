package group.tonight.vipvideohelper.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.socks.library.KLog;

import java.io.IOException;
import java.util.List;

import group.tonight.vipvideohelper.App;
import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.dao.AppRoomDatabase;
import group.tonight.vipvideohelper.dao.VipApiUrl;
import group.tonight.vipvideohelper.other.CheckTask;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiManageActivity extends BaseBackActivity implements BaseQuickAdapter.OnItemChildClickListener {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_manage);

        setTitle("接口管理");

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mBaseQuickAdapter);
        mBaseQuickAdapter.setOnItemChildClickListener(this);

        LiveData<List<VipApiUrl>> liveData = AppRoomDatabase.get().vipApiUrlDao().getAllVipApiUrlsLiveData();
        liveData.observe(ApiManageActivity.this, new Observer<List<VipApiUrl>>() {
            @Override
            public void onChanged(@Nullable List<VipApiUrl> vipApiUrls) {
                if (vipApiUrls == null) {
                    return;
                }
                mBaseQuickAdapter.replaceData(vipApiUrls);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_test:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new CheckTask(AppRoomDatabase.get().vipApiUrlDao().getAllVipApiUrls(), mProgressBar).execute();
                    }
                }).start();
                break;
            case R.id.action_test_1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new CheckTask(AppRoomDatabase.get().vipApiUrlDao().getAllAvailableApiUrls(), mProgressBar).execute();
                    }
                }).start();
                break;
            case R.id.action_test_2:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new CheckTask(AppRoomDatabase.get().vipApiUrlDao().getAllUnAvailableApiUrls(), mProgressBar).execute();
                    }
                }).start();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_api_manage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private BaseQuickAdapter<VipApiUrl, BaseViewHolder> mBaseQuickAdapter = new BaseQuickAdapter<VipApiUrl, BaseViewHolder>(R.layout.list_item_api_manage) {
        @Override
        protected void convert(final BaseViewHolder helper, VipApiUrl item) {
            helper.setChecked(android.R.id.checkbox, item.getStatus() == VipApiUrl.STATUS_NORMAL);
            helper.setText(android.R.id.text1, item.getUrl());
            helper.addOnClickListener(R.id.test);
        }
    };

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, final View view, int position) {
        final VipApiUrl vipApiUrl = mBaseQuickAdapter.getItem(position);
        if (vipApiUrl == null) {
            return;
        }
        final String url = vipApiUrl.getUrl();
        KLog.e("要测试的接口：" + url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = App.getOkHttpClient();
                okHttpClient
                        .newCall(
                                new Request.Builder()
                                        .get()
                                        .url(url)
                                        .build()
                        )
                        .enqueue(
                                new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        vipApiUrl.setStatus(VipApiUrl.STATUS_UNUSED);
                                        vipApiUrl.setUpdateTime(System.currentTimeMillis());
                                        AppRoomDatabase.get().vipApiUrlDao().update(vipApiUrl);

                                        KLog.e(e.getMessage());
                                        Looper.prepare();
                                        Toast.makeText(ApiManageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        int code = response.code();
                                        String message = response.message();
                                        KLog.e(code + " " + message);
                                        Looper.prepare();
                                        if (code != 200) {
                                            Toast.makeText(ApiManageActivity.this, message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ApiManageActivity.this, "接口有效", Toast.LENGTH_SHORT).show();
                                        }
                                        Looper.loop();
                                    }
                                }
                        );


            }
        }).start();
    }


}
