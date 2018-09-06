package group.tonight.vipvideohelper.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface VipApiUrlDao {

    @Query("SELECT * from vipapiurl")
    List<VipApiUrl> getAllVipApiUrls();

    @Query("SELECT * from vipapiurl")
    LiveData<List<VipApiUrl>> getAllVipApiUrlsLiveData();

    @Query("SELECT * from vipapiurl WHERE status == 0")
    LiveData<List<VipApiUrl>> getAllAvailableApiUrlsLiveData();

    @Query("SELECT * from vipapiurl WHERE status == 0")
    List<VipApiUrl> getAllAvailableApiUrls();

    @Query("SELECT * from vipapiurl WHERE status == 1")
    List<VipApiUrl> getAllUnAvailableApiUrls();

    @Query("SELECT * from vipapiurl WHERE status == 1")
    LiveData<List<VipApiUrl>> getAllUnAvailableApiUrlsLiveData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VipApiUrl... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VipApiUrl user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<VipApiUrl> userLists);

    @Update
    void update(VipApiUrl... users);

    @Delete
    void delete(VipApiUrl... users);

    @Query("SELECT * FROM vipapiurl WHERE vipapiurl.url == :url")
    VipApiUrl findVipApiUrlByUrl(String url);
}
