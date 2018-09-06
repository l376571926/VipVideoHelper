package group.tonight.vipvideohelper.dao;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class VipApiUrl {
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_UNUSED = 1;
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String url;
    private int status;
    private long createTime;
    private long updateTime;

    public VipApiUrl() {
        createTime = System.currentTimeMillis();
        updateTime = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
