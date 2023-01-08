package android.redcall.mvvm.viewmodel;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.redcall.R;
import android.redcall.mvvm.model.Record;
import android.redcall.mvvm.repository.RecordRepository;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordViewModel extends AndroidViewModel {

    private final RecordRepository recordRepository;

    private String currentDate, currentTime, timeCall;
    private int second = 0;

    public RecordViewModel(@NonNull Application application) {
        super(application);
        recordRepository = new RecordRepository(application);
    }

    public LiveData<List<Record>> getAllRecords(){
        return recordRepository.getAllRecords();
    }

    public void addRecord(Record record){
        recordRepository.insertRecord(record);
    }

    public void deleteRecord(Record record){
        recordRepository.deleteRecord(record);
    }

    //Запись файла в хранилище
    public File writeFile(String nameNum) {
        currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH.mm.ss", Locale.getDefault()).format(new Date());
        String name = nameNum + " " + currentDate + "_" + currentTime + ".mp3";
        File file = new File(android.os.Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/download", name);
        return file;
    }

    //Получение файла из хранилища
    public File readFile(String nameFile) {
        File readFile = new File(android.os.Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/download", nameFile);
        return readFile;
    }

    //Время звонка
    public void runTime(Handler handler){
        second = 0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                int min = second/60;
                int sec = second - min*60;
                second++;
                timeCall = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(this, 1000);
            }
        });
    }

    //Передаёт данные в Список
    public void addToSpisok(String nameNum){
        String name = nameNum + " " + currentDate + "_" + currentTime + ".mp3";
        int starImagePath = R.drawable.ic_star_empty;
        Record record = new Record(name, timeCall, starImagePath);
        addRecord(record);
    }




}
