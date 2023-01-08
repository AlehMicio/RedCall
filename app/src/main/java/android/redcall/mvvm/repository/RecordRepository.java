package android.redcall.mvvm.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.redcall.mvvm.dao.RecordDao;
import android.redcall.mvvm.database.RecordDatabase;
import android.redcall.mvvm.model.Record;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RecordRepository {

    private final RecordDao recordDao;

    public RecordRepository(Application application){
        RecordDatabase database = RecordDatabase.getInstance(application);
        recordDao = database.getRecordDao();
    }

    //Получить все записи
    public LiveData<List<Record>> getAllRecords(){
        return recordDao.getAllRecords();
    }

    public void insertRecord(Record record){
        new InsertRecordAsyncTask(recordDao).execute(record);
    }

    public void deleteRecord(Record record){
        new DeleteRecordAsyncTask(recordDao).execute(record);
    }

    private static class InsertRecordAsyncTask extends AsyncTask<Record, Void, Void>{

        private RecordDao recordDao;

        public InsertRecordAsyncTask(RecordDao recordDao){
            this.recordDao = recordDao;
        }

        @Override
        protected Void doInBackground(Record... records) {
            recordDao.insert(records[0]);
            return null;
        }
    }

    private static class DeleteRecordAsyncTask extends AsyncTask<Record, Void, Void>{

        private RecordDao recordDao;

        public DeleteRecordAsyncTask(RecordDao recordDao){
            this.recordDao = recordDao;
        }

        @Override
        protected Void doInBackground(Record... records) {
            recordDao.delete(records[0]);
            return null;
        }
    }
}
