package group.tonight.vipvideohelper.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import group.tonight.vipvideohelper.App;
import group.tonight.vipvideohelper.R;
import group.tonight.vipvideohelper.model.VersionUpdateBean;
import group.tonight.vipvideohelper.other.VersionUpdateTask;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 历史版本
 */
public class HistoryVersionActivity extends BaseBackActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected int setActivityTitle() {
        return R.string.history_version;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_version);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.shape_recycler_view_divider);
        dividerItemDecoration.setDrawable(drawable);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mBaseQuickAdapter);
        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VersionUpdateBean item = mBaseQuickAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                Intent intent = new Intent(HistoryVersionActivity.this, HistoryVersionDetailActivity.class);
                intent.putExtra("data", item);
                startActivity(intent);
            }
        });

        Request request = new Request.Builder()
                .get()
                .url(VersionUpdateTask.ALL)
                .build();
        App.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(HistoryVersionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    return;
                }
                String json = response.body().string();

                Type type = new TypeToken<List<VersionUpdateBean>>() {
                }.getType();
                final List<VersionUpdateBean> list = new Gson().fromJson(json, type);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBaseQuickAdapter.replaceData(list);
                    }
                });
            }
        });
    }

    private BaseQuickAdapter<VersionUpdateBean, BaseViewHolder> mBaseQuickAdapter = new BaseQuickAdapter<VersionUpdateBean, BaseViewHolder>(android.R.layout.simple_list_item_1) {
        @Override
        protected void convert(BaseViewHolder helper, VersionUpdateBean item) {
            helper.getView(android.R.id.text1).setBackgroundDrawable(ContextCompat.getDrawable(HistoryVersionActivity.this, android.R.drawable.list_selector_background));
            helper.setText(android.R.id.text1, item.getTag_name());
        }
    };
}
