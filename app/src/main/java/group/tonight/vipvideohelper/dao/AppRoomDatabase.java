package group.tonight.vipvideohelper.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {VipApiUrl.class}, version = 1)
public abstract class AppRoomDatabase extends RoomDatabase {
    public abstract VipApiUrlDao vipApiUrlDao();

    private static AppRoomDatabase INSTANCE;


    public static void init(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, "vip_api_url_database")
                            .build();

                }
            }
        }
    }

    public static AppRoomDatabase get() {
        return INSTANCE;
    }
}
