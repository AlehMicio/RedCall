package android.redcall.mvvm.database;

import android.content.Context;
import android.redcall.mvvm.model.Record;
import android.redcall.mvvm.dao.RecordDao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Record.class}, version = 3)
public abstract class RecordDatabase extends RoomDatabase {

   public abstract RecordDao getRecordDao();
   private static RecordDatabase instance;

   public static synchronized RecordDatabase getInstance(Context context) {
      if (instance == null){
         instance = Room.databaseBuilder(context.getApplicationContext(), RecordDatabase.class, "recordsDB")
                 .fallbackToDestructiveMigration()
                 .build();
      }
      return instance;
   }
}
